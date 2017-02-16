package hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util;


import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDump;

import java.io.File;

public interface ParameterDumpReader {

    ParameterDump parseParamDump(File parameterDumpFile);

}
