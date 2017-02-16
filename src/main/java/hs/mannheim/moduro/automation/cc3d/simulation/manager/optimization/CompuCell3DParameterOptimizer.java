package hs.mannheim.moduro.automation.cc3d.simulation.manager.optimization;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBiMap;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDump;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDumpCellType;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDumpModel;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util.ParameterDumpHelper;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util.ParameterDumpHelperImpl;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.config.SimManagerConfig;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.process.ModuroProcessHandler;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.process.ModuroProcessHandlerImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorFactory;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;

/**
 * This optimizer is used for Compucell3D-based optimization
 */
public class CompuCell3DParameterOptimizer implements ParameterOptimizer {

    // Values of the ParameterDump which will be optimized during optimization Process
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

    final String[] CMAES_PARAMETER_FIELD_NAMES_IN_REQUIRED_ORDER = {
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

    @Override
    public void run(ParameterDump p, File compucell3DRunScriptBatchFile,
                    File compucell3DWorkingDirFile,
                    File moduroSimAutomationWorkingDirFile,
                    File moduroPythonProjectDirFile) {

        if (p == null) {
            throw new RuntimeException("No valid parameter dump file has been provided (is null)");
        }

        System.out.println("Setting fields based on initial parameterDump");
        setFieldsBasedOnPrameterDump(p);

        // CMAES optimizer setup
        RandomGenerator randomGen = RandomGeneratorFactory.createRandomGenerator(new Random());
        Boolean generateStatistics = false;
        SimplePointChecker pointChecker = new SimplePointChecker(SimManagerConfig.CMAES_SIMPLE_POINT_CHECKER_RELATIVE_THRESHOLD,
                SimManagerConfig.CMAES_SIMPLE_POINT_CHECKER_ABSOLUTE_THRESHOLD);

        CMAESOptimizer cmaesOptimizer = new CMAESOptimizer(SimManagerConfig.CMAES_OPTIMIZER_MAX_ITERATIONS,
                SimManagerConfig.CMAES_OPTIMIZER_STOP_AT_FITNESS,
                SimManagerConfig.CMAES_OPTIMIZER_IS_ACTIVE_CMA,
                SimManagerConfig.CMAES_OPTIMIZER_DIAGONAL_ONLY_ITERATION_COUNT,
                SimManagerConfig.CMAES_OPTIMIZER_CHECK_FEASABLE_COUNT, randomGen, generateStatistics,
                pointChecker);


        // final setup to access parameterDump inside "internal fitness" function
        final ParameterDump finalBaseParameterDump = p;

        // To access fields of Compucell3DParameterOptimizer (required to setup Compucell3D simulations)
        CompuCell3DParameterOptimizer mClazz = this;

        // Setup moduro process handler, which will manage the Simulation Context and run the simulations
        final ModuroProcessHandler moduroProcessHandlerImpl = new ModuroProcessHandlerImpl(
                compucell3DRunScriptBatchFile, compucell3DWorkingDirFile, moduroSimAutomationWorkingDirFile,
                moduroPythonProjectDirFile);

        /**
         * UroFunction as internal fitness function
         *
         * Provide optimized parameters for building
         * NEWParameterDump-Files in JSON-Format for starting them in cc3d.
         *
         * Tooks values from cc3d-WorkingDirectory which are running with JSON-based
         * optimized data and calculates an internal fitness for giving it back to cmaes
         */
        ObjectiveFunction internalFitnessFunction = new ObjectiveFunction(new MultivariateFunction() {
            @Override
            public double value(double[] doubles) {
                System.out.println("Iterating internalFitnessFunction");
                System.out.println("Building new ParameterDump based on optimized values");
                ParameterDump optimizedValuesParameterDump = getOptimizedValuesParameterDump(finalBaseParameterDump,
                        doubles);

                System.out.println("Running CC3D");
                moduroProcessHandlerImpl.runSimulation(optimizedValuesParameterDump);
                System.out.println("CC3D is done, will check working directory and parse FitnessPlot.dat");
                Collection<Double> fitnessValuesFromDir = getFitnessValuesFromDir(compucell3DWorkingDirFile);
                System.out.println("Loaded fitnessValues. Calculating values.");
                return calculateInternalFitness(fitnessValuesFromDir);
            }

            private double calculateInternalFitness(Collection<Double> simulationFitnessValues) {
                final Double ERROR_RESULT_DOUBLE = 0.0;

                System.out.println("Calculating Fitness for this run...");
                if (simulationFitnessValues == null || simulationFitnessValues.size() == 0) {
                    System.err.println("No valid set of Data (FitnessPlot.dat) has been provided for this run. " +
                            "Will return 0.0 as result.");
                    return ERROR_RESULT_DOUBLE;
                }

                double averageOfFitnessValues = 0.0d;
                for (Double simulationFitnessValue : simulationFitnessValues) {
                    averageOfFitnessValues += simulationFitnessValue;
                }

                averageOfFitnessValues /= simulationFitnessValues.size();
                System.out.println("Average fitness for this simulation is " + averageOfFitnessValues);

                // result also depends on how many days of simulation we could deliver.
                //teh result does not only depend on the fitness but also on the steps
                //are made during simulation process.
                double internalFitnessResult = averageOfFitnessValues *
                        ((double)simulationFitnessValues.size() / (double)SimManagerConfig.CMAES_MAXIMUM_FITNESS_PLOT_LINES);

                System.out.println("Final internal fitness for this Run is: " + internalFitnessResult);
                return internalFitnessResult;
            }

            /**
             * Get latest directory child.
             * Used to find the latest simulation folder in the compucell working dir.
             * @param parentDir the compucell working directory
             * @return file which is the latest modified directory
             */
            private File getLatestDirectory(File parentDir) {
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

            /**
             *
             * Loads actual Fitnesslot.dat in cc3dWorkingDir
             * Extracts all fitness-values from List
             * @param currentSimulationWorkingDirectory
             * @return
             */
            private Collection<Double> getFitnessValuesFromDir(File currentSimulationWorkingDirectory) {
                File latestSimDir = getLatestDirectory(currentSimulationWorkingDirectory);

                if (latestSimDir == null || !latestSimDir.exists() || latestSimDir.isFile()) {
                    throw new RuntimeException("Could not find valid simulation directory to locate FitnessPlot.dat");
                }

                File fitnessPlotFile = new File(latestSimDir, SimManagerConfig.FILENAMES_FITNESSPLOT);
                if (!fitnessPlotFile.exists()) {
                    // this might happen if a simulation crashes
                    return new ArrayList<>();
                }
                try {
                    List<String> fitnessPlotLines = FileUtils.readLines(fitnessPlotFile, Charset.defaultCharset());
                    Collection<Double> extractedFitnessValues = Collections2.transform(fitnessPlotLines,
                            new Function<String, Double>() {
                                @Override
                                public Double apply(String s) {
                                    // scheme always looks like "2.0 0.34553752096", value behind space is the fitness
                                    return Double.parseDouble(s.split(" ")[1]);
                                }
                            });
                    return extractedFitnessValues;
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }

            // below methods are used to generate a new ParameterDump
            private BiMap<Integer, Field> generateBiFieldIndexHashmap() {
                BiMap<Integer, Field> fieldsWithIndexMapResult = HashBiMap.create();
                Integer fieldNameIndex = 0;
                for (String fieldName : CMAES_PARAMETER_FIELD_NAMES_IN_REQUIRED_ORDER) {
                    System.out.println("Adding field " + fieldNameIndex + " as index " + fieldNameIndex);
                    try {
                        fieldsWithIndexMapResult.put(fieldNameIndex, mClazz.getClass().getDeclaredField(fieldName));
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException("No such field" + e.getMessage(), e);
                    }
                    fieldNameIndex++;
                }

                System.out.println("done generating indice map of required deserilazation fields.");
                return fieldsWithIndexMapResult;
            }

            private ParameterDump getOptimizedValuesParameterDump(ParameterDump aParameterDump,
                                                                  double[] optimizedParameters) {
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
                Collection<ParameterDumpCellType> newParameterDumpCellTypes = getParameterDumpCellTypesByFields(
                        aParameterDump, parameterDumpHelper);
                aParameterDump.setParameterDumpCellTypeList(newParameterDumpCellTypes);
                ParameterDumpModel parameterDumpModel = aParameterDump.getParameterDumpModel();
                parameterDumpModel.setAdhEnergy(modelAdhEnergy);
                parameterDumpModel.setAdhFactor(modelAdhFactor);
                return aParameterDump;
            }

            private void extractOptimizedParametersToFields(double[] optimizedParameters,
                                                            BiMap<Integer, Field> fieldIndexHashmap) {
                for (Field field : fieldIndexHashmap.values()) {
                    field.setAccessible(true);
                    try {
                        double valueToSet = optimizedParameters[fieldIndexHashmap.inverse().get(field)];
                        System.out.println(String.format("Setting value of Field %s from %f to %f",
                                field.getName(), field.get(mClazz), valueToSet));
                        field.set(mClazz, valueToSet);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    field.setAccessible(false);
                }
            }

            private Collection<ParameterDumpCellType> getParameterDumpCellTypesByFields(ParameterDump newParameterDump,
                                                                                        ParameterDumpHelper parameterDumpHelper) {
                System.out.println("Generating CellTypes based on current field values");
                ParameterDumpCellType cellTypeStem = parameterDumpHelper.getCellTypeStem(newParameterDump);
                ParameterDumpCellType cellTypeBasal = parameterDumpHelper.getCellTypeBasal(newParameterDump);
                ParameterDumpCellType cellTypeIntermediate = parameterDumpHelper.getCellTypeIntermediate(newParameterDump);
                ParameterDumpCellType cellTypeUmbrella = parameterDumpHelper.getCellTypeUmbrella(newParameterDump);

                // won't be updated
                ParameterDumpCellType cellTypeBasalMembrane = parameterDumpHelper
                        .getCellTypeBasalMembrane(newParameterDump);
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

        });


        // START Initialization of CMAES-Optimizer
        // You can add objects which implement the interface "Optimization Data"
        // Some of the values are optional but some are required.
        // There is no validation if any of the required values is missing so you will notice this when process crashes.
        // So far the optimization call contains all required fields.

        // The values which are used for OptimizationData where not moved to SimManagerConfig.class since it's easier to
        // maintain the values. However - basic setup which is used in the setter of the CMAES-Optimizer can be found there

        // values taken from the original parameterdump will be used as initial values
        double[] initialGuessValues = new double[]{modelAdhEnergy, modelAdhFactor, cellStemGrothVolumePerDay,
                cellStemNecrosisProb,
                cellStemnutrientRequirement, cellStemSurFit, cellStemVolFit, cellBasalGrothVolumePerDay,
                cellBasalNecrosisProb,
                cellBasalnutrientRequirement, cellBasalSurFit, cellBasalVolFit, cellIntermediateGrothVolumePerDay,
                cellIntermediateNecrosisProb, cellIntermediatenutrientRequirement, cellIntermediateSurFit,
                cellIntermediateVolFit, cellUmbrellaGrothVolumePerDay, cellUmbrellaNecrosisProb,
                cellUmbrellaNutrientRequirement, cellUmbrellaSurFit, cellUmbrellaVolFit};

        double[] inputSigmaDoubleArray = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        CMAESOptimizer.Sigma sigma = new CMAESOptimizer.Sigma(inputSigmaDoubleArray);
        InitialGuess initialGuess = new InitialGuess(initialGuessValues);
        // population size: anzahl der zu optimierenden Werte?
        CMAESOptimizer.PopulationSize populationSize = new CMAESOptimizer.PopulationSize(SimManagerConfig.CMAES_POPULATION_SIZE);
        GoalType goalTypeMaximize = GoalType.MAXIMIZE;

        // upper and lower bounds as described in documentation
        double[] lowerBounds = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] upperBounds = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
                Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
                Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
                Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
                Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        SimpleBounds simpleBounds = new SimpleBounds(lowerBounds, upperBounds);
        MaxEval maxEval = new MaxEval(SimManagerConfig.CMAES_MAX_EVALUATION_CALLS);

        System.out.println("Setup for CmaesOptimizer is done. Will start Process");
        PointValuePair optimize = cmaesOptimizer.optimize(internalFitnessFunction, populationSize,
                goalTypeMaximize, initialGuess, sigma, simpleBounds, maxEval);

        System.out.println("Best fitness: " + optimize.getValue());
    }

    private void setFieldsBasedOnPrameterDump(ParameterDump p) {
        ParameterDumpHelper parameterDumpHelper = new ParameterDumpHelperImpl();
        ParameterDumpCellType cellTypeBasal = parameterDumpHelper.getCellTypeBasal(p);
        ParameterDumpCellType cellTypeIntermediate = parameterDumpHelper.getCellTypeIntermediate(p);
        ParameterDumpCellType cellTypeUmbrella = parameterDumpHelper.getCellTypeUmbrella(p);
        ParameterDumpCellType cellTypeStem = parameterDumpHelper.getCellTypeStem(p);

        modelAdhEnergy = p.getParameterDumpModel().getAdhEnergy();
        modelAdhFactor = p.getParameterDumpModel().getAdhFactor();
        cellStemGrothVolumePerDay = cellTypeStem.getGrowthVolumePerDay();
        cellStemNecrosisProb = cellTypeStem.getNecrosisProb();
        cellStemnutrientRequirement = cellTypeStem.getNutrientRequirement();
        cellStemSurFit = cellTypeStem.getSurFit();
        cellStemVolFit = cellTypeStem.getVolFit();

        cellBasalGrothVolumePerDay = cellTypeBasal.getGrowthVolumePerDay();
        cellBasalNecrosisProb = cellTypeBasal.getNecrosisProb();
        cellBasalnutrientRequirement = cellTypeBasal.getNutrientRequirement();
        cellBasalSurFit = cellTypeBasal.getSurFit();
        cellBasalVolFit = cellTypeBasal.getVolFit();

        cellIntermediateGrothVolumePerDay = cellTypeIntermediate.getGrowthVolumePerDay();
        cellIntermediateNecrosisProb = cellTypeIntermediate.getNecrosisProb();
        cellIntermediatenutrientRequirement = cellTypeIntermediate.getNutrientRequirement();
        cellIntermediateSurFit = cellTypeIntermediate.getSurFit();
        cellIntermediateVolFit = cellTypeIntermediate.getVolFit();

        cellUmbrellaGrothVolumePerDay = cellTypeUmbrella.getGrowthVolumePerDay();
        cellUmbrellaNecrosisProb = cellTypeUmbrella.getNecrosisProb();
        cellUmbrellaNutrientRequirement = cellTypeUmbrella.getNutrientRequirement();
        cellUmbrellaSurFit = cellTypeUmbrella.getSurFit();
        cellUmbrellaVolFit = cellTypeUmbrella.getVolFit();
    }

}
