package hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d;

import hs.mannheim.moduro.automation.cc3d.simulation.manager.config.SimManagerConfig;

import java.io.File;


public class Cc3dProcessManagerImpl implements Cc3dProcessManager {

    /**
     * This returns an ProcessBuilder which executes the compucell RunScript.bat.
     * The path will be passed as String.
     * Will set a parameter "-i", that points to the file which will be run by compucell
     *
     * @return a ProcessBuilder that's prepared to run a file with compucell (no gui)
     */
    @Override
    public ProcessBuilder getCompuCellProcess(File runScriptFile, File executingCc3dFile) {
        try {
            System.out.println("Enter getCompuCellProcess()");
            if (!runScriptFile.exists()) {
                throw new RuntimeException("Cannot find execution file." +
                        "Parameter value was: " + runScriptFile.getAbsolutePath());
            }

            // http://stackoverflow.com/questions/17120782/running-bat-file-with-java-processbuilder
            final ProcessBuilder cc3dProc =
                    new ProcessBuilder(
                            runScriptFile.getAbsolutePath(),
                            "-i", executingCc3dFile.getAbsolutePath(),
                            "-f", SimManagerConfig.COMPUCELL_RUNSCRIPT_PARAM_FREQUENCY.toString()).inheritIO();
            return cc3dProc;
        } finally {
            System.out.println("exit getCompuCellProcess()");
        }
    }
}
