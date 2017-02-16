package hs.mannheim.moduro.automation.cc3d.simulation.manager;

import hs.mannheim.moduro.automation.cc3d.simulation.manager.config.SimManagerConfig;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.optimization.CmsParameterOptimizer;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.optimization.MockParameterOptimizerImpl;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.optimization.ParameterOptimizer;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.process.ModuroProcessHandler;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.process.ModuroProcessHandlerImpl;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainClass {

    public static void main(String[] args) {
        try {
            System.out.println("Starting ModUro automation system at " + new Date());

            // Set required Options
            Options simManagerOptions = SimManagerConfig.getCliOptions();
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd;

            try {
                // http://stackoverflow.com/questions/7341683/parsing-arguments-to-a-java-command-line-program
                cmd = parser.parse(simManagerOptions, args);

                String pathToRunScriptString = cmd.getOptionValue(SimManagerConfig.OPTIONS_PARAMETER_RUNSCRIPT_PATH_FULL);
                File runScriptFile = new File(pathToRunScriptString);
                if (!runScriptFile.exists() || runScriptFile.isDirectory()) {
                    throw new RuntimeException("RunScript file is missing or value is not a file." + runScriptFile.getAbsolutePath());
                }

                String pathToParameterDumpString = cmd.getOptionValue(
                        SimManagerConfig.OPTIONS_PARAMETER_PARAM_DUMP_PATH_FULL);
                File parameterDumpFile = new File(pathToParameterDumpString);
                if (!parameterDumpFile.exists() || parameterDumpFile.isDirectory()) {
                    throw new RuntimeException("ParameterDump or Model Json is missing or invalid. " + parameterDumpFile.getAbsolutePath());
                }

                String pathToCompucellWorkingDir = cmd.getOptionValue(SimManagerConfig.OPTIONS_PARAMETER_CC3D_WORKDIR_PATH_FULL);
                File compuCellWorkDirFile = new File(pathToCompucellWorkingDir);
                if (!compuCellWorkDirFile.exists() || compuCellWorkDirFile.isFile()) {
                    throw new RuntimeException("Compucell working dir does not exist or is Invalid: "
                            + compuCellWorkDirFile.getAbsolutePath());

                }
                String moduroAutoWorkingDir = cmd.getOptionValue(SimManagerConfig.OPTIONS_PARAMETER_MODURO_SIM_AUTOMATION_WORKDIR_PATH_FULL);
                File moduroAutoWorkDirFile = new File(moduroAutoWorkingDir);


                if (!moduroAutoWorkDirFile.exists() || moduroAutoWorkDirFile.isFile()) {
                    throw new RuntimeException("Working directory for hs-ma-moduro-automation-cc3d-simulation-manager" +
                            " is invalid or does not exist: " + moduroAutoWorkDirFile.getAbsolutePath());
                }

                String pathToModuroSimPythonScript = cmd.getOptionValue(SimManagerConfig.OPTIONS_PARAMETER_MODURO_SIMULATIONS_PYTHON_SCRIPT_PATH_FULL);
                File moduroSimulationsPythonScriptFile = new File(pathToModuroSimPythonScript);
                if (!moduroSimulationsPythonScriptFile.exists() || moduroSimulationsPythonScriptFile.isDirectory()) {
                    throw new RuntimeException("Path to simulation Script is not valid or file does not exist: "
                            + moduroSimulationsPythonScriptFile.getAbsolutePath());
                }

                ModuroProcessHandler moduroProcessHandler = new ModuroProcessHandlerImpl(
                        runScriptFile, compuCellWorkDirFile, moduroAutoWorkDirFile, moduroSimulationsPythonScriptFile
                );

                final String targetOptimizationName = cmd.getOptionValue(SimManagerConfig.OPTIONS_PARAMETER_MODURO_SIMULATIONS_OPTIMIZATION_ALGORITHM_FULL);
                System.out.println("Looking for OptimizationAlgorithm by name: " + targetOptimizationName);
                final Map<String, ParameterOptimizer> availableParameterOptimizers = registerParameterOptimizers();
                if (!availableParameterOptimizers.containsKey(targetOptimizationName)) {
                    throw new RuntimeException("There is no optimizationParameter registered by name: " + targetOptimizationName);
                }
                final ParameterOptimizer targetParameterOptimizer = availableParameterOptimizers.get(targetOptimizationName);
                System.out.println("ParameterOptimizer is available: " + targetOptimizationName);
                // aufgrund umbau auskommentiert
                // moduroProcessHandler.runSimulation(parameterDumpFile, targetParameterOptimizer);
            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } finally {
            System.out.println("Exit ModUro automation system: " + new Date());
        }
    }

    private static Map<String, ParameterOptimizer> registerParameterOptimizers() {
        System.out.println("Registering parameterOptimizers");
        Map<String, ParameterOptimizer> result = new HashMap<>();
        ParameterOptimizer mockParameterOptimizerImpl = new MockParameterOptimizerImpl();
        System.out.println(String.format("Registering OptimizationAlgorithm by name: %s",
                mockParameterOptimizerImpl.getOptimizerName()));
        result.put(mockParameterOptimizerImpl.getOptimizerName(), mockParameterOptimizerImpl);
        ParameterOptimizer cmaesUroFunctionParameterOptimizerImpl = new CmsParameterOptimizer();
        System.out.println(String.format("Registering OptimizationAlgorithm by name: %s",
                cmaesUroFunctionParameterOptimizerImpl.getOptimizerName()));
        result.put(cmaesUroFunctionParameterOptimizerImpl.getOptimizerName(),
                cmaesUroFunctionParameterOptimizerImpl);
        System.out.println("Registered ParameterOptimizer count: " + result.size());
        return result;
    }

}
