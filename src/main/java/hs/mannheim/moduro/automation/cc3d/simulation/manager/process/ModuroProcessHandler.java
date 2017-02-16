package hs.mannheim.moduro.automation.cc3d.simulation.manager.process;


import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDump;

public interface ModuroProcessHandler {
    void runSimulation(ParameterDump optimizedParameterDump);
}
