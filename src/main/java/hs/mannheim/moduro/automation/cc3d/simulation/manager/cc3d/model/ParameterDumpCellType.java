package hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model;


import hs.mannheim.moduro.automation.cc3d.simulation.manager.config.SimManagerConfig;
import org.apache.commons.lang3.StringUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterDumpCellType extends ParameterDumpBaseComponent implements ParameterDumpEntry {

    @ParameterDumpValue(key = "apoptosisTimeInDays", type = ParameterDumpValue.ParameterDumpValueType.DOUBLE)
    private Double apoptosisTimeInDays;

    @ParameterDumpValue(key = "consumPerCell", type = ParameterDumpValue.ParameterDumpValueType.DOUBLE)
    private Double consumPerCell;

    @ParameterDumpValue(key = "descendants,descendantsString", type = ParameterDumpValue.ParameterDumpValueType.STRING)
    private String descendantsString;

    @ParameterDumpValue(key = "divides", type = ParameterDumpValue.ParameterDumpValueType.BOOLEAN)
    private Boolean divides;

    @ParameterDumpValue(key = "frozen", type = ParameterDumpValue.ParameterDumpValueType.BOOLEAN)
    private Boolean frozen;

    @ParameterDumpValue(key = "growthVolumePerDay", type = ParameterDumpValue.ParameterDumpValueType.DOUBLE)
    private Double growthVolumePerDay;

    @ParameterDumpValue(key = "id", type = ParameterDumpValue.ParameterDumpValueType.INTEGER)
    private Integer id;

    @ParameterDumpValue(key = "maxDiameter", type = ParameterDumpValue.ParameterDumpValueType.INTEGER)
    private Integer maxDiameter;

    @ParameterDumpValue(key = "maxVol", type = ParameterDumpValue.ParameterDumpValueType.DOUBLE)
    private Double maxVol;

    @ParameterDumpValue(key = "minDiameter", type = ParameterDumpValue.ParameterDumpValueType.INTEGER)
    private Integer minDiameter;

    @ParameterDumpValue(key = "minVol", type = ParameterDumpValue.ParameterDumpValueType.DOUBLE)
    private Double minVol;

    @ParameterDumpValue(key = "name", type = ParameterDumpValue.ParameterDumpValueType.STRING)
    private String name;

    @ParameterDumpValue(key = "necrosisProb", type = ParameterDumpValue.ParameterDumpValueType.DOUBLE)
    private Double necrosisProb;

    @ParameterDumpValue(key = "nutrientRequirement", type = ParameterDumpValue.ParameterDumpValueType.DOUBLE)
    private Double nutrientRequirement;

    @ParameterDumpValue(key = "surFit", type = ParameterDumpValue.ParameterDumpValueType.DOUBLE)
    private Double surFit;

    @ParameterDumpValue(key = "volFit", type = ParameterDumpValue.ParameterDumpValueType.DOUBLE)
    private Double volFit;

    private List<ParameterDumpCellTypeDescendant> descendantsList;


    public ParameterDumpCellType(Map<String, String> parsedBlock) throws IllegalAccessException {
        super(parsedBlock);
        this.descendantsList = new ArrayList<>();

        // convert value of field descendantsString into structured data
        if (StringUtils.isBlank(this.getDescendantsString())
                || StringUtils.equals(this.getDescendantsString(), "[]")) {
            System.out.println(String.format("No descendants defined for CellType %s", getName()));
            return;
        }

        System.out.println(String.format("Setting Descendant values of CellType %s", getName()));
        final Pattern pattern = Pattern.compile(SimManagerConfig.CELLTYPE_DECENDANTS_REGEX);
        final Matcher m = pattern.matcher(this.getDescendantsString());
        final List<String> groups = new ArrayList<>();
        while (m.find()) {
            groups.add(m.group());
        }


        for (String group : groups) {
            System.out.println("Generate CellTypeDescendant entry based on String:" + group);
            this.descendantsList.add(new ParameterDumpCellTypeDescendant(group));
        }
    }

    public ParameterDumpCellType() {
        System.out.println("Called Default Constructor of ParameterDumpCellType()");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CellType:").append("\n");
        sb.append(super.toString());
        return sb.toString();
    }

    // todo: impl
    @Override
    public Boolean isComplete() {
        throw new NotImplementedException();
    }

    public Double getApoptosisTimeInDays() {
        return apoptosisTimeInDays;
    }

    public void setApoptosisTimeInDays(Double apoptosisTimeInDays) {
        this.apoptosisTimeInDays = apoptosisTimeInDays;
    }

    public Double getConsumPerCell() {
        return consumPerCell;
    }

    public void setConsumPerCell(Double consumPerCell) {
        this.consumPerCell = consumPerCell;
    }

    public String getDescendantsString() {
        return descendantsString;
    }

    public void setDescendantsString(String descendantsString) {
        this.descendantsString = descendantsString;
    }

    public Boolean getDivides() {
        return divides;
    }

    public void setDivides(Boolean divides) {
        this.divides = divides;
    }

    public Boolean getFrozen() {
        return frozen;
    }

    public void setFrozen(Boolean frozen) {
        this.frozen = frozen;
    }

    public Double getGrowthVolumePerDay() {
        return growthVolumePerDay;
    }

    public void setGrowthVolumePerDay(Double growthVolumePerDay) {
        this.growthVolumePerDay = growthVolumePerDay;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMaxDiameter() {
        return maxDiameter;
    }

    public void setMaxDiameter(Integer maxDiameter) {
        this.maxDiameter = maxDiameter;
    }

    public Double getMaxVol() {
        return maxVol;
    }

    public void setMaxVol(Double maxVol) {
        this.maxVol = maxVol;
    }

    public Integer getMinDiameter() {
        return minDiameter;
    }

    public void setMinDiameter(Integer minDiameter) {
        this.minDiameter = minDiameter;
    }

    public Double getMinVol() {
        return minVol;
    }

    public void setMinVol(Double minVol) {
        this.minVol = minVol;
    }


    public Double getNecrosisProb() {
        return necrosisProb;
    }

    public void setNecrosisProb(Double necrosisProb) {
        this.necrosisProb = necrosisProb;
    }

    public Double getNutrientRequirement() {
        return nutrientRequirement;
    }

    public void setNutrientRequirement(Double nutrientRequirement) {
        this.nutrientRequirement = nutrientRequirement;
    }

    public Double getSurFit() {
        return surFit;
    }

    public void setSurFit(Double surFit) {
        this.surFit = surFit;
    }

    public Double getVolFit() {
        return volFit;
    }

    public void setVolFit(Double volFit) {
        this.volFit = volFit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

