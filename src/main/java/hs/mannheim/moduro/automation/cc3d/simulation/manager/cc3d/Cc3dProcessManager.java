package hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d;


import java.io.File;

public interface Cc3dProcessManager {
    ProcessBuilder getCompuCellProcess(File pathToCompucell, File executingCc3dFile);
}
