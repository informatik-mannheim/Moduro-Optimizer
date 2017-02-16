package hs.mannheim.moduro.automation.cc3d.simulation.manager.optimization;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBiMap;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDump;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDumpCellType;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDumpModel;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util.ParameterDumpHelper;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util.ParameterDumpHelperImpl;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.config.SimManagerConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CmsParameterOptimizer implements ParameterOptimizer {

    public static final double NO_RESULT_FITNESS_DEFAULT_VALUE = -1.0d;
    /***
     *
     * Folgende Parameter einer ParameterDump.dat müssen durch dem CmaesOptimizer optimiert werden:
     *
     * ExecConfig: NONE
     * SdBpaCdiInDa(Model):[adhEnergy,adhFactor]
     * CellType ("Medium"): NONE
     * CellType ("BasalMembrane"): NONE
     * CellType ("Stem"): [growthVolumePerDay(double),necrosisProb(double),nutrientRequirement(double),surFit(double),volFit(double)]
     *  CellType ("Basal"): [growthVolumePerDay(double),necrosisProb(double),nutrientRequirement(double),surFit(double),volFit(double)]
     * CellType ("Intermediate"): [growthVolumePerDay(double),necrosisProb(double),nutrientRequirement(double),surFit(double),volFit(double)]
     * CellType ("Umbrella"): [growthVolumePerDay(double),necrosisProb(double),nutrientRequirement(double),surFit(double),volFit(double)]
     **/
    private static final String OPTIMIZER_CMAES_URO_NAME = "cmaes-uro";

    private Double modelAdhEnergy;
    private Double modelAdhFactor;

    private Double cellStemGrothVolumePerDay;
    private Double cellStemNecrosisProb;
    private Double cellStemnutrientRequirement;
    private Double cellStemSurFit;
    private Double cellStemVolFit;

    private Double cellBasalGrothVolumePerDay;
    private Double cellBasalNecrosisProb;
    private Double cellBasalnutrientRequirement;
    private Double cellBasalSurFit;
    private Double cellBasalVolFit;

    private Double cellIntermediateGrothVolumePerDay;
    private Double cellIntermediateNecrosisProb;
    private Double cellIntermediatenutrientRequirement;
    private Double cellIntermediateSurFit;
    private Double cellIntermediateVolFit;

    private Double cellUmbrellaGrothVolumePerDay;
    private Double cellUmbrellaNecrosisProb;
    private Double cellUmbrellaNutrientRequirement;
    private Double cellUmbrellaSurFit;
    private Double cellUmbrellaVolFit;

    private final String[] CMAES_PARAMETER_FIELD_NAMES_IN_REQUIRED_ORDER = {
            "modelAdhEnergy",
            "modelAdhFactor",
            "cellStemGrothVolumePerDay",
            "cellStemNecrosisProb",
            "cellStemnutrientRequirement",
            "cellStemSurFit",
            "cellStemVolFit",
            "cellBasalGrothVolumePerDay",
            "cellBasalNecrosisProb",
            "cellBasalnutrientRequirement",
            "cellBasalSurFit",
            "cellBasalVolFit",
            "cellIntermediateGrothVolumePerDay",
            "cellIntermediateNecrosisProb",
            "cellIntermediatenutrientRequirement",
            "cellIntermediateSurFit",
            "cellIntermediateVolFit",
            "cellUmbrellaGrothVolumePerDay",
            "cellUmbrellaNecrosisProb",
            "cellUmbrellaNutrientRequirement",
            "cellUmbrellaSurFit",
            "cellUmbrellaVolFit"};


    /***
     * Enters the latest CC3D SimDir and calculates Fitness based on the FitnessPlot.dat file
     *
     * A Simulation has to run before this method is called.
     * The Simulation has to be finished at the point the method gets called
     *
     * Requires the FitnessPlot.dat file to exists
     * TYI: The FitnessPlot.dat will be written in a "stepper" method which is implemented in the
     * ModuroCC3D Python N
     *
     * @param cc3dWorkingDir
     * @return
     */
    @Override
    public Double getInternalFitnessOfLatestCC3DSimulation(File cc3dWorkingDir) {
        if (cc3dWorkingDir == null || cc3dWorkingDir.isFile()) {
            throw new RuntimeException("Invalid folder set to calculate internal fitness: "
                    + cc3dWorkingDir != null ? cc3dWorkingDir.getAbsolutePath() : "null");
        }

        File currentSimulationWorkingDirectory = getLastestDirectory(cc3dWorkingDir);
        Collection<Double> simulationFitnessValues =  getFitnessValuesFromDir(currentSimulationWorkingDirectory);
        if (simulationFitnessValues.size() == 0) {
            System.err.println(String.format("WARNING: there could no values be etracted from Directory %s/%s" ,
                    cc3dWorkingDir,SimManagerConfig.FILENAMES_FITNESSPLOT));
           return NO_RESULT_FITNESS_DEFAULT_VALUE;
        }

        // todo: analyze values from simulationFitnessValues and calculate a fitness value.
        throw new RuntimeException("NOT IMPLEMENTED YET");
    }

    private Collection<Double> getFitnessValuesFromDir(File currentSimulationWorkingDirectory){
        File fitnessPlotFile = new File(currentSimulationWorkingDirectory, SimManagerConfig.FILENAMES_FITNESSPLOT);
        if (!fitnessPlotFile.exists()) {
            // this might happen if a simulation crashes
            return new ArrayList<>();
        }
        try {
            List<String> fitnessPlotLines = FileUtils.readLines(fitnessPlotFile, Charset.defaultCharset());
            Collection<Double> extractedFitnessValues = Collections2.transform(fitnessPlotLines, new Function<String, Double>() {
                @Override
                public Double apply(String s) {
                    // scheme always looks like "2.0 0.34553752096", value behind space is the fitness
                    return Double.parseDouble(s.split(" ")[1]);
                }
            });
            return extractedFitnessValues;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /***
     * Finds the latest childDir in a parent dir.
     * @param parentDir
     * @return
     */
    private File getLastestDirectory(File parentDir) {
        File[] childDirs = parentDir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }

        });

        File latestDir = null;
        for (File childDir : childDirs) {
            if (latestDir == null || childDir.lastModified() > latestDir.lastModified()) {
                latestDir = childDir;
            }
        }

        if (childDirs == null) {
            throw new RuntimeException("No ChildDir found inside Parent Dir: " + parentDir.getAbsolutePath());
        }

        System.out.println("Found latest CC3D Workdir Simulation dir: " + latestDir.getAbsolutePath());
        return latestDir;
    }

    @Override
    public String getOptimizerName() {
        return OPTIMIZER_CMAES_URO_NAME;
    }

    public double[] getCmaFunctionParameters() {
        System.out.println("Loading parameters for cmaes optimization based on fields parsed from ParameterDump.dat.");
        BiMap<Integer, Field> indexFieldBiMap = generateBiFieldIndexHashmap();
        List<Double> fieldValuesList = new ArrayList<>();
        for (Field field : indexFieldBiMap.values()) {
            try {
                field.setAccessible(true);
                Object valueOfField = field.get(this);
                if (!(valueOfField instanceof Double)) {
                    throw new RuntimeException(String.format("%s not instance of Double", field.getName()));
                }
                fieldValuesList.add((Double) valueOfField);
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        // cms optimizer requires primitive type
        // source: http://stackoverflow.com/questions/6018267/how-to-cast-from-listdouble-to-double-in-java
        return fieldValuesList.stream().mapToDouble(d -> d).toArray();
    }

    private BiMap<Integer, Field> generateBiFieldIndexHashmap() {
        BiMap<Integer, Field> fieldsWithIndexMapResult = HashBiMap.create();
        Integer fieldNameIndex = 0;
        for (String fieldName : CMAES_PARAMETER_FIELD_NAMES_IN_REQUIRED_ORDER) {
            System.out.println("Adding field " + fieldNameIndex + " as index " + fieldNameIndex);
            try {
                fieldsWithIndexMapResult.put(fieldNameIndex, this.getClass().getDeclaredField(fieldName));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            fieldNameIndex++;
        }

        System.out.println("done generating indice map of required deserilazation fields.");
        return fieldsWithIndexMapResult;
    }

    private ParameterDump transformToParameterDump(ParameterDump aParameterDump, double[] optimizedParameters) {
        System.out.println("Generating optimized ParameterDump, based on optimized Parameters");

        if (aParameterDump == null) {
            throw new RuntimeException("Origin ParameterDump is missing");
        }

        if (optimizedParameters == null) {
            throw new RuntimeException("OptimizedParameters are missing");
        }

        BiMap<Integer, Field> fieldIndexHashmap = generateBiFieldIndexHashmap();
        if (optimizedParameters.length != fieldIndexHashmap.values().size()) {
            throw new RuntimeException(
                    String.format("OptimizedParameters size: %s doesn't match with required size: %s",
                            optimizedParameters.length, fieldIndexHashmap.size()));
        }

        // set optimized values as new field values.
        extractOptimizedParametersToFields(optimizedParameters, fieldIndexHashmap);

        // create parameterDump from field values
        ParameterDumpHelper parameterDumpHelper = new ParameterDumpHelperImpl();
        Collection<ParameterDumpCellType> newParameterDumpCellTypes =
                getParameterDumpCellTypesByFields(aParameterDump, parameterDumpHelper);
        aParameterDump.setParameterDumpCellTypeList(newParameterDumpCellTypes);
        ParameterDumpModel parameterDumpModel = aParameterDump.getParameterDumpModel();
        parameterDumpModel.setAdhEnergy(modelAdhEnergy);
        parameterDumpModel.setAdhFactor(modelAdhFactor);
        return aParameterDump;
    }

    private void extractOptimizedParametersToFields(double[] optimizedParameters, BiMap<Integer, Field> fieldIndexHashmap) {
        for (Field field : fieldIndexHashmap.values()) {
            field.setAccessible(true);
            try {
                double valueToSet = optimizedParameters[fieldIndexHashmap.inverse().get(field)];
                System.out.println(String.format("Setting value of Field %s from %f to %f",
                        field.getName(), field.get(this), valueToSet));
                field.set(this, valueToSet);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            field.setAccessible(false);
        }
    }

    /***
     * Builds Collection of CellTypes based on the current fields of CmaesOptimizerImpl.
     * Make sure you have run the optimization methods before calling this method.
     * @param newParameterDump
     * @param parameterDumpHelper
     */
    private Collection<ParameterDumpCellType> getParameterDumpCellTypesByFields(ParameterDump newParameterDump, ParameterDumpHelper parameterDumpHelper) {
        System.out.println("Generating CellTypes based on current field values");
        ParameterDumpCellType cellTypeStem = parameterDumpHelper.getCellTypeStem(newParameterDump);
        ParameterDumpCellType cellTypeBasal = parameterDumpHelper.getCellTypeBasal(newParameterDump);
        ParameterDumpCellType cellTypeIntermediate = parameterDumpHelper.getCellTypeIntermediate(newParameterDump);
        ParameterDumpCellType cellTypeUmbrella = parameterDumpHelper.getCellTypeUmbrella(newParameterDump);

        // won't be updated
        ParameterDumpCellType cellTypeBasalMembrane = parameterDumpHelper.getCellTypeBasalMembrane(newParameterDump);
        ParameterDumpCellType cellTypeMedium = parameterDumpHelper.getCellTypeMedium(newParameterDump);

        cellTypeStem.setGrowthVolumePerDay(cellStemGrothVolumePerDay);
        cellTypeStem.setNecrosisProb(cellStemNecrosisProb);
        cellTypeStem.setNutrientRequirement(cellStemnutrientRequirement);
        cellTypeStem.setSurFit(cellStemSurFit);
        cellTypeStem.setVolFit(cellStemVolFit);

        cellTypeBasal.setGrowthVolumePerDay(cellBasalGrothVolumePerDay);
        cellTypeBasal.setNecrosisProb(cellBasalNecrosisProb);
        cellTypeBasal.setNutrientRequirement(cellBasalnutrientRequirement);
        cellTypeBasal.setSurFit(cellBasalSurFit);
        cellTypeBasal.setVolFit(cellBasalVolFit);

        cellTypeIntermediate.setGrowthVolumePerDay(cellIntermediateGrothVolumePerDay);
        cellTypeIntermediate.setNecrosisProb(cellIntermediateNecrosisProb);
        cellTypeIntermediate.setNutrientRequirement(cellIntermediatenutrientRequirement);
        cellTypeIntermediate.setSurFit(cellIntermediateSurFit);
        cellTypeIntermediate.setVolFit(cellIntermediateVolFit);

        cellTypeUmbrella.setGrowthVolumePerDay(cellUmbrellaGrothVolumePerDay);
        cellTypeUmbrella.setNecrosisProb(cellUmbrellaNecrosisProb);
        cellTypeUmbrella.setNutrientRequirement(cellUmbrellaNutrientRequirement);
        cellTypeUmbrella.setSurFit(cellUmbrellaSurFit);
        cellTypeUmbrella.setVolFit(cellUmbrellaVolFit);

        return Arrays.asList(cellTypeStem, cellTypeBasal, cellTypeIntermediate, cellTypeUmbrella,
                cellTypeBasalMembrane, cellTypeMedium);
    }

    private void extractParameterDumpValuesToFields(ParameterDump originParameterDump) {
        try {
            System.out.println("Loading ParameterDump data...");
            modelAdhEnergy = originParameterDump.getParameterDumpModel().getAdhEnergy();
            modelAdhFactor = originParameterDump.getParameterDumpModel().getAdhFactor();

            // CELL TYPE STEM START
            Collection<ParameterDumpCellType> stemCellTypeCollection =
                    Collections2.filter(originParameterDump.getParameterDumpCellTypeList(),
                            new Predicate<ParameterDumpCellType>() {
                                @Override
                                public boolean apply(ParameterDumpCellType parameterDumpCellType) {
                                    return StringUtils.equalsIgnoreCase(parameterDumpCellType.getName(), "Stem");
                                }
                            });
            ParameterDumpCellType cellTypeStem = (ParameterDumpCellType) stemCellTypeCollection.toArray()[0];
            cellStemGrothVolumePerDay = cellTypeStem.getGrowthVolumePerDay();
            cellStemNecrosisProb = cellTypeStem.getNecrosisProb();
            cellStemnutrientRequirement = cellTypeStem.getNutrientRequirement();
            cellStemSurFit = cellTypeStem.getSurFit();
            cellStemVolFit = cellTypeStem.getVolFit();
            // CELL TYPE STEM END

            // CELL TYPE UMBRELLA START
            Collection<ParameterDumpCellType> umbrellaCellTypeCollection = Collections2.filter(originParameterDump.getParameterDumpCellTypeList(), new Predicate<ParameterDumpCellType>() {
                @Override
                public boolean apply(ParameterDumpCellType parameterDumpCellType) {
                    return StringUtils.equalsIgnoreCase(parameterDumpCellType.getName(), "Umbrella");
                }
            });
            ParameterDumpCellType cellTypeUmbrella = (ParameterDumpCellType) umbrellaCellTypeCollection.toArray()[0];
            cellUmbrellaGrothVolumePerDay = cellTypeUmbrella.getGrowthVolumePerDay();
            cellUmbrellaNecrosisProb = cellTypeUmbrella.getNecrosisProb();
            cellUmbrellaNutrientRequirement = cellTypeUmbrella.getNutrientRequirement();
            cellUmbrellaSurFit = cellTypeUmbrella.getSurFit();
            cellUmbrellaVolFit = cellTypeUmbrella.getVolFit();
            // CELL TYPE UMBRELLA END

            // CELL TYPE BASAL START
            Collection<ParameterDumpCellType> basalCellTypeCollection = Collections2.filter(originParameterDump.getParameterDumpCellTypeList(), new Predicate<ParameterDumpCellType>() {
                @Override
                public boolean apply(ParameterDumpCellType parameterDumpCellType) {
                    return StringUtils.equalsIgnoreCase(parameterDumpCellType.getName(), "Basal");
                }
            });
            ParameterDumpCellType cellTypeBasal = (ParameterDumpCellType) umbrellaCellTypeCollection.toArray()[0];
            cellBasalGrothVolumePerDay = cellTypeBasal.getGrowthVolumePerDay();
            cellBasalNecrosisProb = cellTypeBasal.getNecrosisProb();
            cellBasalnutrientRequirement = cellTypeBasal.getNutrientRequirement();
            cellBasalSurFit = cellTypeBasal.getSurFit();
            cellBasalVolFit = cellTypeBasal.getVolFit();
            // CELL TYPE BASAL END

            // CELL TYPE INTERMEDIATE START
            Collection<ParameterDumpCellType> intermediateCellTypeCollection = Collections2.filter(originParameterDump.getParameterDumpCellTypeList(), new Predicate<ParameterDumpCellType>() {
                @Override
                public boolean apply(ParameterDumpCellType parameterDumpCellType) {
                    return StringUtils.equalsIgnoreCase(parameterDumpCellType.getName(), "Intermediate");
                }
            });
            ParameterDumpCellType cellTypeIntermediate = (ParameterDumpCellType) intermediateCellTypeCollection.toArray()[0];
            cellIntermediateGrothVolumePerDay = cellTypeIntermediate.getGrowthVolumePerDay();
            cellIntermediateNecrosisProb = cellTypeIntermediate.getNecrosisProb();
            cellIntermediatenutrientRequirement = cellTypeIntermediate.getNutrientRequirement();
            cellIntermediateSurFit = cellTypeIntermediate.getSurFit();
            cellIntermediateVolFit = cellTypeIntermediate.getVolFit();
            // CELL TYPE INTERMEDIATE END

        } finally {
            System.out.println("exit extractParameterDumpValuesToFields");
        }
    }

    public Double getModelAdhEnergy() {
        return modelAdhEnergy;
    }

    public void setModelAdhEnergy(Double modelAdhEnergy) {
        this.modelAdhEnergy = modelAdhEnergy;
    }

    public Double getModelAdhFactor() {
        return modelAdhFactor;
    }

    public void setModelAdhFactor(Double modelAdhFactor) {
        this.modelAdhFactor = modelAdhFactor;
    }

    public Double getCellStemGrothVolumePerDay() {
        return cellStemGrothVolumePerDay;
    }

    public void setCellStemGrothVolumePerDay(Double cellStemGrothVolumePerDay) {
        this.cellStemGrothVolumePerDay = cellStemGrothVolumePerDay;
    }

    public Double getCellStemNecrosisProb() {
        return cellStemNecrosisProb;
    }

    public void setCellStemNecrosisProb(Double cellStemNecrosisProb) {
        this.cellStemNecrosisProb = cellStemNecrosisProb;
    }

    public Double getCellStemnutrientRequirement() {
        return cellStemnutrientRequirement;
    }

    public void setCellStemnutrientRequirement(Double cellStemnutrientRequirement) {
        this.cellStemnutrientRequirement = cellStemnutrientRequirement;
    }

    public Double getCellStemSurFit() {
        return cellStemSurFit;
    }

    public void setCellStemSurFit(Double cellStemSurFit) {
        this.cellStemSurFit = cellStemSurFit;
    }

    public Double getCellStemVolFit() {
        return cellStemVolFit;
    }

    public void setCellStemVolFit(Double cellStemVolFit) {
        this.cellStemVolFit = cellStemVolFit;
    }

    public Double getCellBasalGrothVolumePerDay() {
        return cellBasalGrothVolumePerDay;
    }

    public void setCellBasalGrothVolumePerDay(Double cellBasalGrothVolumePerDay) {
        this.cellBasalGrothVolumePerDay = cellBasalGrothVolumePerDay;
    }

    public Double getCellBasalNecrosisProb() {
        return cellBasalNecrosisProb;
    }

    public void setCellBasalNecrosisProb(Double cellBasalNecrosisProb) {
        this.cellBasalNecrosisProb = cellBasalNecrosisProb;
    }

    public Double getCellBasalnutrientRequirement() {
        return cellBasalnutrientRequirement;
    }

    public void setCellBasalnutrientRequirement(Double cellBasalnutrientRequirement) {
        this.cellBasalnutrientRequirement = cellBasalnutrientRequirement;
    }

    public Double getCellBasalSurFit() {
        return cellBasalSurFit;
    }

    public void setCellBasalSurFit(Double cellBasalSurFit) {
        this.cellBasalSurFit = cellBasalSurFit;
    }

    public Double getCellBasalVolFit() {
        return cellBasalVolFit;
    }

    public void setCellBasalVolFit(Double cellBasalVolFit) {
        this.cellBasalVolFit = cellBasalVolFit;
    }

    public Double getCellIntermediateGrothVolumePerDay() {
        return cellIntermediateGrothVolumePerDay;
    }

    public void setCellIntermediateGrothVolumePerDay(Double cellIntermediateGrothVolumePerDay) {
        this.cellIntermediateGrothVolumePerDay = cellIntermediateGrothVolumePerDay;
    }

    public Double getCellIntermediateNecrosisProb() {
        return cellIntermediateNecrosisProb;
    }

    public void setCellIntermediateNecrosisProb(Double cellIntermediateNecrosisProb) {
        this.cellIntermediateNecrosisProb = cellIntermediateNecrosisProb;
    }

    public Double getCellIntermediatenutrientRequirement() {
        return cellIntermediatenutrientRequirement;
    }

    public void setCellIntermediatenutrientRequirement(Double cellIntermediatenutrientRequirement) {
        this.cellIntermediatenutrientRequirement = cellIntermediatenutrientRequirement;
    }

    public Double getCellIntermediateSurFit() {
        return cellIntermediateSurFit;
    }

    public void setCellIntermediateSurFit(Double cellIntermediateSurFit) {
        this.cellIntermediateSurFit = cellIntermediateSurFit;
    }

    public Double getCellIntermediateVolFit() {
        return cellIntermediateVolFit;
    }

    public void setCellIntermediateVolFit(Double cellIntermediateVolFit) {
        this.cellIntermediateVolFit = cellIntermediateVolFit;
    }

    public Double getCellUmbrellaGrothVolumePerDay() {
        return cellUmbrellaGrothVolumePerDay;
    }

    public void setCellUmbrellaGrothVolumePerDay(Double cellUmbrellaGrothVolumePerDay) {
        this.cellUmbrellaGrothVolumePerDay = cellUmbrellaGrothVolumePerDay;
    }

    public Double getCellUmbrellaNecrosisProb() {
        return cellUmbrellaNecrosisProb;
    }

    public void setCellUmbrellaNecrosisProb(Double cellUmbrellaNecrosisProb) {
        this.cellUmbrellaNecrosisProb = cellUmbrellaNecrosisProb;
    }

    public Double getCellUmbrellaNutrientRequirement() {
        return cellUmbrellaNutrientRequirement;
    }

    public void setCellUmbrellaNutrientRequirement(Double cellUmbrellaNutrientRequirement) {
        this.cellUmbrellaNutrientRequirement = cellUmbrellaNutrientRequirement;
    }

    public Double getCellUmbrellaSurFit() {
        return cellUmbrellaSurFit;
    }

    public void setCellUmbrellaSurFit(Double cellUmbrellaSurFit) {
        this.cellUmbrellaSurFit = cellUmbrellaSurFit;
    }

    public Double getCellUmbrellaVolFit() {
        return cellUmbrellaVolFit;
    }

    public void setCellUmbrellaVolFit(Double cellUmbrellaVolFit) {
        this.cellUmbrellaVolFit = cellUmbrellaVolFit;
    }

    @Deprecated
    private double[] getCmaFunctionParameterManually() {
        // this method has been replaced by getCmaFunctionParameters()
        System.err.println("entering getCmaFunctionParameterManually(). This is for test purposes only.");
        return new double[]{
                modelAdhEnergy,
                modelAdhFactor,
                cellStemGrothVolumePerDay,
                cellStemNecrosisProb,
                cellStemnutrientRequirement,
                cellStemSurFit,
                cellStemVolFit,
                cellBasalGrothVolumePerDay,
                cellBasalNecrosisProb,
                cellBasalnutrientRequirement,
                cellBasalSurFit,
                cellBasalVolFit,
                cellIntermediateGrothVolumePerDay,
                cellIntermediateNecrosisProb,
                cellIntermediatenutrientRequirement,
                cellIntermediateSurFit,
                cellIntermediateVolFit,
                cellUmbrellaGrothVolumePerDay,
                cellUmbrellaNecrosisProb,
                cellUmbrellaNutrientRequirement,
                cellUmbrellaSurFit,
                cellUmbrellaVolFit,
        };
    }

}

