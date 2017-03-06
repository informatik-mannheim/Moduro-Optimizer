package hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util;


import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDump;

import java.io.File;

// TODO Is this interface needed? Not really, but it does not matter.
public interface ParameterDumpReader {

    ParameterDump parseParamDump(File parameterDumpFile);

}
