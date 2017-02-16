package hs.mannheim.moduro.automation.cc3d.simulation.manager.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/***
 * This class contains static configurations
 */
public class SimManagerConfig {


    public static final String OPTIONS_PARAMETER_RUNSCRIPT_PATH_SHORT = "c";
    public static final String OPTIONS_PARAMETER_RUNSCRIPT_PATH_FULL = "runscript";
    public static final String OPTIONS_PARAMETER_RUNSCRIPT_PATH_DESCRIPTION = "Path to CompuCell3D runScript.bat file";
    public static final Boolean OPTIONS_PARAMETER_RUNSCRIPT_PATH_HAS_ARG = true;
    public static final Boolean OPTIONS_PARAMETER_RUNSCRIPT_PATH_REQUIRED = true;

    public static final String OPTIONS_PARAMETER_PARAM_DUMP_PATH_SHORT = "p";
    public static final String OPTIONS_PARAMETER_PARAM_DUMP_PATH_FULL = "parameterDump";
    public static final String OPTIONS_PARAMETER_PARAM_DUMP_PATH_DESCRIPTION = "Path to ParameterDump filewhich ";
    public static final Boolean OPTIONS_PARAMETER_PARAM_DUMP_PATH_HAS_ARG = true;
    public static final Boolean OPTIONS_PARAMETER_PARAM_DUMP_PATH_REQUIRED = true;

    public static final String OPTIONS_PARAMETER_CC3D_WORKDIR_PATH_SHORT = "w";
    public static final String OPTIONS_PARAMETER_CC3D_WORKDIR_PATH_FULL = "cc3d-workingdir";
    public static final String OPTIONS_PARAMETER_CC3D_WORKDIR_PATH_DESCRIPTION = "Path to CC3D Workingdir";
    public static final Boolean OPTIONS_PARAMETER_CC3D_WORKDIR_PATH_HAS_ARG = true;
    public static final Boolean OPTIONS_PARAMETER_CC3D_WORKDIR_PATH_REQUIRED = true;

    public static final String OPTIONS_PARAMETER_MODURO_SIM_AUTOMATION_WORKDIR_PATH_SHORT = "t";
    public static final String OPTIONS_PARAMETER_MODURO_SIM_AUTOMATION_WORKDIR_PATH_FULL = "moduro-simulation-workingdir";
    public static final String OPTIONS_PARAMETER_MODURO_SIM_AUTOMATION_WORKDIR_PATH_DESCRIPTION = "Path to Moduro Workingdir";
    public static final Boolean OPTIONS_PARAMETER_MODURO_SIM_AUTOMATION_WORKDIR_PATH_HAS_ARG = true;
    public static final Boolean OPTIONS_PARAMETER_MODURO_SIM_AUTOMATION_WORKDIR_PATH_REQUIRED = true;

    public static final String OPTIONS_PARAMETER_MODURO_SIMULATIONS_PYTHON_SCRIPT_PATH_SHORT = "s";
    public static final String OPTIONS_PARAMETER_MODURO_SIMULATIONS_PYTHON_SCRIPT_PATH_FULL = "moduro-python-scripra";
    public static final String OPTIONS_PARAMETER_MODURO_SIMULATIONS_PYTHON_SCRIPT_PATH_DESCRIPTION = "Path to the Moduro Simulation Python Scripts";
    public static final Boolean OPTIONS_PARAMETER_MODURO_SIMULATIONS_PYTHON_SCRIPT_PATH_HAS_ARG = true;
    public static final Boolean OPTIONS_PARAMETER_MODURO_SIMULATIONS_PYTHON_SCRIPT_PATH_REQUIRED = true;

    public static final String OPTIONS_PARAMETER_MODURO_SIMULATIONS_OPTIMIZATION_ALGORITHM_SHORT = "a";
    public static final String OPTIONS_PARAMETER_MODURO_SIMULATIONS_OPTIMIZATION_ALGORITHM_FULL = "optimization-algorithm";
    public static final String OPTIONS_PARAMETER_MODURO_SIMULATIONS_OPTIMIZATION_ALGORITHM_DESCRIPTION = "Name of the algorithm which should be used for optimization.";
    public static final Boolean OPTIONS_PARAMETER_MODURO_SIMULATIONS_OPTIMIZATION_ALGORITHM_HAS_ARG = true;
    public static final Boolean OPTIONS_PARAMETER_MODURO_SIMULATIONS_OPTIMIZATION_ALGORITHM_REQUIRED = true;

    public static final String FILENAMES_FITNESSPLOT = "FitnessPlot.dat";


    // todo klären ob 1000 steps für die LatticeData in Ordnung sind.
    public static final Integer COMPUCELL_RUNSCRIPT_PARAM_FREQUENCY = 1000;

    public static final String CELL_ID_1 = "CellId1";
    public static final String CELL_ID_2 = "CellId2";

    public static final String CELLTYPE_DECENDANTS_REGEX = "(\\[[0-9]\\.[0-9][0-9]?\\,?\\s?\\[?\\d?\\,?\\s?\\d?\\]?\\]?)";


    public static Options getCliOptions() {
        Options options = new Options();

        Option runScriptPathOption = new Option(OPTIONS_PARAMETER_RUNSCRIPT_PATH_SHORT, OPTIONS_PARAMETER_RUNSCRIPT_PATH_FULL,
                OPTIONS_PARAMETER_RUNSCRIPT_PATH_HAS_ARG, OPTIONS_PARAMETER_RUNSCRIPT_PATH_DESCRIPTION);
        runScriptPathOption.setRequired(SimManagerConfig.OPTIONS_PARAMETER_RUNSCRIPT_PATH_REQUIRED);
        options.addOption(runScriptPathOption);


        Option parameterDumpPathOption = new Option(OPTIONS_PARAMETER_PARAM_DUMP_PATH_SHORT, OPTIONS_PARAMETER_PARAM_DUMP_PATH_FULL,
                OPTIONS_PARAMETER_PARAM_DUMP_PATH_HAS_ARG, OPTIONS_PARAMETER_PARAM_DUMP_PATH_DESCRIPTION);
        runScriptPathOption.setRequired(SimManagerConfig.OPTIONS_PARAMETER_PARAM_DUMP_PATH_REQUIRED);
        options.addOption(parameterDumpPathOption);

        Option cc3dWorkingDirOption = new Option(OPTIONS_PARAMETER_CC3D_WORKDIR_PATH_SHORT, OPTIONS_PARAMETER_CC3D_WORKDIR_PATH_FULL,
                OPTIONS_PARAMETER_CC3D_WORKDIR_PATH_HAS_ARG, OPTIONS_PARAMETER_CC3D_WORKDIR_PATH_DESCRIPTION);
        runScriptPathOption.setRequired(SimManagerConfig.OPTIONS_PARAMETER_CC3D_WORKDIR_PATH_REQUIRED);
        options.addOption(cc3dWorkingDirOption);

        Option moduroSimAutomationWorkingDirOption = new Option(OPTIONS_PARAMETER_MODURO_SIM_AUTOMATION_WORKDIR_PATH_SHORT, OPTIONS_PARAMETER_MODURO_SIM_AUTOMATION_WORKDIR_PATH_FULL,
                OPTIONS_PARAMETER_MODURO_SIM_AUTOMATION_WORKDIR_PATH_HAS_ARG, OPTIONS_PARAMETER_MODURO_SIM_AUTOMATION_WORKDIR_PATH_DESCRIPTION);
        runScriptPathOption.setRequired(SimManagerConfig.OPTIONS_PARAMETER_MODURO_SIM_AUTOMATION_WORKDIR_PATH_REQUIRED);
        options.addOption(moduroSimAutomationWorkingDirOption);

        Option moduroSimulationPythonDirOption = new Option(OPTIONS_PARAMETER_MODURO_SIMULATIONS_PYTHON_SCRIPT_PATH_SHORT,
                OPTIONS_PARAMETER_MODURO_SIMULATIONS_PYTHON_SCRIPT_PATH_FULL,
                OPTIONS_PARAMETER_MODURO_SIMULATIONS_PYTHON_SCRIPT_PATH_HAS_ARG,
                OPTIONS_PARAMETER_MODURO_SIMULATIONS_PYTHON_SCRIPT_PATH_DESCRIPTION);
        runScriptPathOption.setRequired(SimManagerConfig.OPTIONS_PARAMETER_MODURO_SIMULATIONS_PYTHON_SCRIPT_PATH_REQUIRED);
        options.addOption(moduroSimulationPythonDirOption);

        Option moduroTargetOptimizationAlgorithmOption = new Option(OPTIONS_PARAMETER_MODURO_SIMULATIONS_OPTIMIZATION_ALGORITHM_SHORT,
                OPTIONS_PARAMETER_MODURO_SIMULATIONS_OPTIMIZATION_ALGORITHM_FULL,
                OPTIONS_PARAMETER_MODURO_SIMULATIONS_OPTIMIZATION_ALGORITHM_HAS_ARG,
                OPTIONS_PARAMETER_MODURO_SIMULATIONS_OPTIMIZATION_ALGORITHM_DESCRIPTION);
        runScriptPathOption.setRequired(OPTIONS_PARAMETER_MODURO_SIMULATIONS_OPTIMIZATION_ALGORITHM_REQUIRED);
        options.addOption(moduroTargetOptimizationAlgorithmOption);

        return options;
    }
}
