package hs.mannheim.moduro.automation.cc3d.simulation.manager.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/***
 * This class contains statis configurations
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

    public static final String FILENAMES_FITNESSPLOT = "FitnessPlot.dat";


    // This parameter will be used in CompuCell and passed in RunScript.bat
    /***
     * allows to specify how often
     vtk files are stored to the disk. Those files tend to be quite large for bigger simulations so
     storing them every single MCS (default setting) slows down simulation considerably and
     also uses a lot of disk space
     */
    public static final Integer COMPUCELL_RUNSCRIPT_PARAM_FREQUENCY = 1000;

    public static final String CELL_ID_1 = "CellId1";
    public static final String CELL_ID_2 = "CellId2";

    // Regex, um die Descendants werte zu einem Celltype in der ParameterDump zu parsen
    public static final String CELLTYPE_DECENDANTS_REGEX = "(\\[[0-9]\\.[0-9][0-9]?\\,?\\s?\\[?\\d?\\,?\\s?\\d?\\]?\\]?)";

    // CMAES related config
    // There are also required "OptimizationData"-Values which are not stored here but in CompuCell3DParameterOptimizer.class
    public static Integer CMAES_OPTIMIZER_MAX_ITERATIONS = 1999999999;
    public static Double CMAES_OPTIMIZER_STOP_AT_FITNESS = 1.00;
    public static Boolean CMAES_OPTIMIZER_IS_ACTIVE_CMA = true;
    public static Integer CMAES_OPTIMIZER_DIAGONAL_ONLY_ITERATION_COUNT = 1; // todo: value ok?  // diagonalOnly - Number of initial iterations, where the covariance matrix remains diagonal.
    public static Integer CMAES_OPTIMIZER_CHECK_FEASABLE_COUNT = 1; // todo: value ok? // // checkFeasableCount - Determines how often new random objective variables are  generated in case they are out of bounds.

    public static Double CMAES_SIMPLE_POINT_CHECKER_RELATIVE_THRESHOLD = -1.0; // todo: werte ok?
    public static Double CMAES_SIMPLE_POINT_CHECKER_ABSOLUTE_THRESHOLD = -2.0; // todo: werte ok?
    public static Integer CMAES_MAXIMUM_FITNESS_PLOT_LINES = 1440;
    public static Integer CMAES_MAX_EVALUATION_CALLS = 10;
    public static Integer CMAES_POPULATION_SIZE = 6;

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

        return options;
    }
}
