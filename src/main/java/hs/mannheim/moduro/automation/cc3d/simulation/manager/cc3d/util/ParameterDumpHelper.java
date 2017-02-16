package hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util;


import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDump;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDumpCellType;

public interface ParameterDumpHelper {

    ParameterDumpCellType getCellTypeMedium(ParameterDump parameterDump);
    ParameterDumpCellType getCellTypeBasalMembrane(ParameterDump parameterDump);
    ParameterDumpCellType getCellTypeStem(ParameterDump parameterDump);
    ParameterDumpCellType getCellTypeBasal(ParameterDump parameterDump);
    ParameterDumpCellType getCellTypeIntermediate(ParameterDump parameterDump);
    ParameterDumpCellType getCellTypeUmbrella(ParameterDump parameterDump);


}
