package hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model;


import hs.mannheim.moduro.automation.cc3d.simulation.manager.config.SimManagerConfig;

import java.util.HashMap;
import java.util.Map;

public class ParameterDumpCellTypeDescendant {

    private Double probability;
    private Integer cellId1;
    private Integer cellId2;

    public ParameterDumpCellTypeDescendant(Double propability, Integer cellId1, Integer cellId2) {
        this.cellId1 = cellId1;
        this.cellId2 = cellId2;
        this.probability = propability;
    }

    public ParameterDumpCellTypeDescendant(String parseString) {
        // todo: validate parseString
        this.probability = getProbabilityValue(parseString);
        Map<String, Integer> cellIds = getCellIds(parseString);
        this.cellId1 = cellIds.get(SimManagerConfig.CELL_ID_1);
        this.cellId2 = cellIds.get(SimManagerConfig.CELL_ID_2);
    }

    public Integer getCellId1() {
        return cellId1;
    }

    public void setCellId1(Integer cellId1) {
        this.cellId1 = cellId1;
    }

    public Integer getCellId2() {
        return cellId2;
    }

    public void setCellId2(Integer cellId2) {
        this.cellId2 = cellId2;
    }




    private static Double getProbabilityValue(String group) {
        // group looks like [0.9, [2, 3]]
        String propabilityAsString = group.split(",")[0].substring(1);
        return Double.parseDouble(propabilityAsString);
    }

    private static Map<String, Integer> getCellIds(String group) {
        // group looks like [0.9, [2, 3]]
        String replaceBracketRegexOpen = "\\[";
        String replaceBracketRegexClosed = "\\]";
        String EMPTY = "";
        group = group.replaceAll(replaceBracketRegexOpen, EMPTY);
        group = group.replaceAll(replaceBracketRegexClosed, EMPTY);
        String[] commaSplit = group.split(",");

        if (commaSplit.length <= 2) {
            throw new RuntimeException("Illegal size of isolated descendant values");
        }

        Integer cellId2 = Integer.parseInt(commaSplit[commaSplit.length - 1].trim());
        Integer cellId1 = Integer.parseInt(commaSplit[commaSplit.length - 2].trim());

        Map<String, Integer> result = new HashMap<>();
        result.put(SimManagerConfig.CELL_ID_1, cellId1);
        result.put(SimManagerConfig.CELL_ID_2, cellId2);
        return result;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }
}
