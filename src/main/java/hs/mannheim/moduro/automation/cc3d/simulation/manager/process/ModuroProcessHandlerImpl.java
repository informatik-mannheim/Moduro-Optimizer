package hs.mannheim.moduro.automation.cc3d.simulation.manager.process;

import com.google.gson.Gson;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.Cc3dProcessManager;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.Cc3dProcessManagerImpl;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDump;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util.xml.ModuroXmlHelperImpl;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

// todo: ausbauen / umbauen
public class ModuroProcessHandlerImpl implements ModuroProcessHandler {
    private File compucell3DRunScriptBatchFile;
    private File compucell3DWorkingDirFile;
    private File moduroSimAutomationWorkingDir;
    private File moduroPythonProjectDir;

    private Cc3dProcessManager cc3dProcessManager;

    /***
     * This class manages the whole automation process workflow.
     *
     * - It converts JSON files or ParameterDumps into entities - It optimizes the values of the converted enities - It
     * generates JSON files which are later loaded by the moduro python scripts - It generated cc3d files to run a
     * simulation - It runs the generated project which compucell3d - When the simulation is done for any reason
     * (success, error, abort) - it will repeat this process
     *
     * @param compucell3DRunScriptBatchFile Path to the runScript.bat file of Compucell3D
     * @param compucell3DWorkingDirFile Path to the Compucell3DWorking directory
     * @param moduroSimAutomationWorkingDir Path to the working dir of the automation tool
     * @param moduroPythonProjectDir Path of the moduro python projects (to prepare simulations)
     */
    public ModuroProcessHandlerImpl(File compucell3DRunScriptBatchFile,
                                    File compucell3DWorkingDirFile, File moduroSimAutomationWorkingDir,
                                    File moduroPythonProjectDir) {
        this.compucell3DRunScriptBatchFile = compucell3DRunScriptBatchFile;
        this.compucell3DWorkingDirFile = compucell3DWorkingDirFile;
        this.moduroSimAutomationWorkingDir = moduroSimAutomationWorkingDir;
        this.moduroPythonProjectDir = moduroPythonProjectDir;
        this.cc3dProcessManager = new Cc3dProcessManagerImpl();
    }

    @Override
    public void runSimulation(ParameterDump parameterDump) {
        try {
            if (parameterDump == null) {
                throw new RuntimeException("Invalid ParameterDump: ParameterDump is null");
            }
            System.out.println("Generating directory to store data that is used to run the current simulation.");
            File targetSimDir = generateSimulationTargetDir(parameterDump);

            if (targetSimDir == null || !targetSimDir.exists()) {
                throw new RuntimeException("Could not create target Simulation dir. Parent Dir is: "
                        + moduroSimAutomationWorkingDir.getAbsolutePath());
            }

            System.out.println("New target dir for this simulation: " + targetSimDir.getAbsolutePath());
            System.out.println("Converting Model Setup to JSON.");
            final File modelJsonFile = exportParameterDump(parameterDump, targetSimDir);
            System.out.println("Saving target configuration file to final output.");
            final File cc3dTargetSimFile = new File(targetSimDir, "runJson.cc3d");
            exportCC3DFile(modelJsonFile, cc3dTargetSimFile);
            System.out.println("Preperations to run simulation complete. Running Compucell");

            ProcessBuilder compuCellProcess = cc3dProcessManager.getCompuCellProcess(compucell3DRunScriptBatchFile,
                    cc3dTargetSimFile);
            compuCellProcess.redirectOutput(new File(targetSimDir, "simulation.log"));
            System.out.println("Starting Compucell simulation, waiting until finished.");
            Process runningCompucellProcess = compuCellProcess.start();
            runningCompucellProcess.waitFor();
            System.out.println("Compucell Simulation is done.");

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            System.out.println("Exit runSimulation()");
        }
    }

    private void exportCC3DFile(File modelJsonFile, File cc3dTargetSimFile) {
        System.out.println("Generating CC3D File at: " + cc3dTargetSimFile.getAbsolutePath());
        final ModuroXmlHelperImpl moduroXmlHelper = new ModuroXmlHelperImpl();
        moduroXmlHelper.generateCompucellSimulationFile(cc3dTargetSimFile,
                modelJsonFile, moduroPythonProjectDir);
        System.out.println("Generated cc3d file");
    }

    private File exportParameterDump(ParameterDump parameterDump, File targetSimDir) {
        try {
            System.out.println("Enter exportParameterDump()");
            if (targetSimDir == null) {
                throw new RuntimeException("No moduro automation simulation dir has been found. Aborting Process");
            }
            if (parameterDump == null) {
                throw new RuntimeException("No parameterDump has been submitted. Aborting Process.");
            }
            if (!targetSimDir.exists()) {
                throw new RuntimeException("Missing Directory:" + targetSimDir + " . Aborting Process");
            }

            System.out
                    .println(String.format("EXporting ParameterDump File as Json to: %s", targetSimDir.getAbsolutePath()));
            System.out.println("IMPORTANT. WE WILL USE THE DEFAULT CHARSET TO SAVE THE JSON STRING: "
                    + Charset.defaultCharset().displayName());

            Gson gson = new Gson();
            final String jsonString = gson.toJson(parameterDump);
            File jsonTargetFile = new File(targetSimDir, "moduroCC3DModellConfiguration.json");
            FileUtils.writeStringToFile(jsonTargetFile, jsonString, Charset.defaultCharset());
            System.out.println("ParameterDump has been exported.");
            return jsonTargetFile;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            System.out.println("Exit exportParameterDump()");
        }
    }

    private File generateSimulationTargetDir(ParameterDump parameterDump) {
        System.out.println("Creating folder for current simulation");
        String targetSimNameString = parameterDump.getParameterDumpModel().getName();
        String newSimPathString = moduroSimAutomationWorkingDir.getAbsolutePath() + File.separator + targetSimNameString
                + File.separator + new Date().getTime();
        System.out.println("TargetDir for this simulation run will be " + newSimPathString);
        File newSimConfigFile = new File(newSimPathString);
        boolean isCreatedSimDir = newSimConfigFile.mkdirs();
        System.out.println("Directory " + newSimConfigFile.getAbsolutePath() + "created: " + isCreatedSimDir);
        return newSimConfigFile;
    }

    public File getCompucell3DRunScriptBatchFile() {
        return compucell3DRunScriptBatchFile;
    }

    public void setCompucell3DRunScriptBatchFile(File compucell3DRunScriptBatchFile) {
        this.compucell3DRunScriptBatchFile = compucell3DRunScriptBatchFile;
    }

    public File getCompucell3DWorkingDirFile() {
        return compucell3DWorkingDirFile;
    }

    public void setCompucell3DWorkingDirFile(File compucell3DWorkingDirFile) {
        this.compucell3DWorkingDirFile = compucell3DWorkingDirFile;
    }

    public File getModuroSimAutomationWorkingDir() {
        return moduroSimAutomationWorkingDir;
    }

    public void setModuroSimAutomationWorkingDir(File moduroSimAutomationWorkingDir) {
        this.moduroSimAutomationWorkingDir = moduroSimAutomationWorkingDir;
    }

}
