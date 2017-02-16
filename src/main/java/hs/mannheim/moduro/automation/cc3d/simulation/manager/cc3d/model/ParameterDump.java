package hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model;


import java.util.Collection;

public class ParameterDump {

    private String startTime;
    private ParameterDumpExecConfig parameterDumpExecConfig;
    private  ParameterDumpModel parameterDumpModel;
    private Collection<ParameterDumpCellType> parameterDumpCellTypeList;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public ParameterDumpExecConfig getParameterDumpExecConfig() {
        return parameterDumpExecConfig;
    }

    public void setParameterDumpExecConfig(ParameterDumpExecConfig parameterDumpExecConfig) {
        this.parameterDumpExecConfig = parameterDumpExecConfig;
    }

    public ParameterDumpModel getParameterDumpModel() {
        return parameterDumpModel;
    }

    public void setParameterDumpModel(ParameterDumpModel parameterDumpModel) {
        this.parameterDumpModel = parameterDumpModel;
    }

    public Collection<ParameterDumpCellType> getParameterDumpCellTypeList() {
        return parameterDumpCellTypeList;
    }

    public void setParameterDumpCellTypeList(Collection<ParameterDumpCellType> parameterDumpCellTypeList) {
        this.parameterDumpCellTypeList = parameterDumpCellTypeList;
    }

}
