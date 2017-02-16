package hs.mannheim.moduro.automation.cc3d.simulation.manager;

import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDump;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util.ParameterDumpReader;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util.ParameterDumpReaderImpl;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.config.SimManagerConfig;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.optimization.CompuCell3DParameterOptimizer;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.optimization.ParameterOptimizer;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.Date;

public class MainClass {

    public static void main(String[] args) {
        try {
            System.out.println("Starting ModUro automation system at date: " + new Date());

            Options simManagerOptions = SimManagerConfig.getCliOptions();
            CommandLineParser parser = new DefaultParser();
            CommandLine commandLine;

            try {
                // http://stackoverflow.com/questions/7341683/parsing-arguments-to-a-java-command-line-program
                commandLine = parser.parse(simManagerOptions, args);

                String pathToRunScriptString = commandLine.getOptionValue(
                        SimManagerConfig.OPTIONS_PARAMETER_RUNSCRIPT_PATH_FULL);
                File runScriptFile = new File(pathToRunScriptString);
                if (!runScriptFile.exists() || runScriptFile.isDirectory()) {
                    throw new RuntimeException("RunScript file is missing or value is not a file."
                            + runScriptFile.getAbsolutePath());
                }

                String pathToParameterDumpString = commandLine.getOptionValue(
                        SimManagerConfig.OPTIONS_PARAMETER_PARAM_DUMP_PATH_FULL);
                File parameterDumpFile = new File(pathToParameterDumpString);
                if (!parameterDumpFile.exists() || parameterDumpFile.isDirectory()) {
                    throw new RuntimeException("ParameterDump or Model Json is missing or invalid. "
                            + parameterDumpFile.getAbsolutePath());
                }

                String pathToCompucellWorkingDir = commandLine.getOptionValue(
                        SimManagerConfig.OPTIONS_PARAMETER_CC3D_WORKDIR_PATH_FULL);
                File compuCellWorkDirFile = new File(pathToCompucellWorkingDir);
                if (!compuCellWorkDirFile.exists() || compuCellWorkDirFile.isFile()) {
                    throw new RuntimeException("Compucell working dir does not exist or is Invalid: "
                            + compuCellWorkDirFile.getAbsolutePath());

                }
                String moduroAutoWorkingDir = commandLine.getOptionValue(
                        SimManagerConfig.OPTIONS_PARAMETER_MODURO_SIM_AUTOMATION_WORKDIR_PATH_FULL);
                File moduroAutoWorkDirFile = new File(moduroAutoWorkingDir);

                if (!moduroAutoWorkDirFile.exists() || moduroAutoWorkDirFile.isFile()) {
                    throw new RuntimeException("Working directory for hs-ma-moduro-automation-cc3d-simulation-manager" +
                            " is invalid or does not exist: " + moduroAutoWorkDirFile.getAbsolutePath());
                }

                String pathToModuroSimPythonScript = commandLine.getOptionValue(
                        SimManagerConfig.OPTIONS_PARAMETER_MODURO_SIMULATIONS_PYTHON_SCRIPT_PATH_FULL);
                File moduroSimulationsPythonScriptFile = new File(pathToModuroSimPythonScript);
                if (!moduroSimulationsPythonScriptFile.exists() || moduroSimulationsPythonScriptFile.isDirectory()) {
                    throw new RuntimeException("Path to simulation Script is not valid or file does not exist: "
                            + moduroSimulationsPythonScriptFile.getAbsolutePath());
                }

                // Parse initial ParameterDump file
                ParameterDumpReader parameterDumpReader = new ParameterDumpReaderImpl();
                final ParameterDump parameterDump = parameterDumpReader.parseParamDump(parameterDumpFile);

                System.out.println("DIR Setup is complete. Initializing CMAES-Process.");
                ParameterOptimizer compuCellCmaesOptimizer = new CompuCell3DParameterOptimizer();
                compuCellCmaesOptimizer.run(parameterDump, runScriptFile, compuCellWorkDirFile,
                        moduroAutoWorkDirFile, moduroSimulationsPythonScriptFile);
            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } finally {
            System.out.println("Exit ModUro automation system at date: " + new Date());
        }
    }


}
