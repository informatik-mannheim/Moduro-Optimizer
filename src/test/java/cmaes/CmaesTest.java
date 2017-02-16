package cmaes;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBiMap;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDump;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDumpCellType;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDumpModel;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util.ParameterDumpHelper;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util.ParameterDumpHelperImpl;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util.ParameterDumpReader;
import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util.ParameterDumpReaderImpl;
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
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;

public class CmaesTest {

   /*
   final String cc3DWorkingDirPathString = "C:" + File.separator + "Users" + File.separator + "Station"
           + File.separator + "CC3DWorkspace";
   final String cc3dRunScriptPathString = "C:\\Program Files (x86)\\CompuCell3D\\runScript.bat";
   final String moduroSimAutomationWorkingDirString = "C:\\Users\\Station\\Documents\\moduro-automation-working-dir";
   final String runJsonCc3D = "C:\\Users\\Station\\PycharmProjects\\Moduro-CC3D\\Simulation\\RunJsonCc3D.py";
   */
   final String cc3DWorkingDirPathString = "c:\\Users\\Markus\\CC3DOptWorkspace\\";
   final String cc3dRunScriptPathString =
           "c:\\Program Files\\CompuCell3D\\runScript.bat";
   final String moduroSimAutomationWorkingDirString =
           cc3DWorkingDirPathString;
   final String runJsonCc3D =
           cc3DWorkingDirPathString;

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

   // doku: https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/
   @Test
   public void cmaesTest() {

      // zu Testzwecken alles in der methode, wenn auch unschoen
      ParameterDump p = null;
      try {
         p = getMockedParameterDump();
      } catch (IOException e) {
         throw new RuntimeException(e.getMessage(), e);
      }

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
            "cellUmbrellaVolFit" };

      // CMAES OPTIMIZER SETUP
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

      // initial parameterDump so we can build a new one within the internal fitness function
      ParameterDump baseParameterDump = null;
      try {
         baseParameterDump = getMockedParameterDump();
      } catch (IOException e) {
         throw new RuntimeException(e.getMessage(), e);
      }

      /***
       * Interne Fitnessfunktion
       *
       * - Liest aktuelles Simulations WorkingDir aus - Läd FitnessPlot.dat - Analysiert Werte und berechnet eigene
       * Fitness
       */
      final ParameterDump finalBaseParameterDump = baseParameterDump;
      // müssen wir mitgeben, damit wir im inneren der internalFitnessFunction auf die
      // Felder der Klasse zugreifen köennen
      CmaesTest mClazz = this;

      File compucell3DRunScriptBatchFile = new File(cc3dRunScriptPathString);
      File compucell3DWorkingDirFile = new File(cc3DWorkingDirPathString);
      File moduroSimAutomationWorkingDirFile = new File(moduroSimAutomationWorkingDirString);
      File moduroPythonProjectDirFile = new File(runJsonCc3D);

      final ModuroProcessHandler moduroProcessHandlerImpl = new ModuroProcessHandlerImpl(
            compucell3DRunScriptBatchFile, compucell3DWorkingDirFile, moduroSimAutomationWorkingDirFile,
            moduroPythonProjectDirFile);

      /***
       * Interne FitnessFunktion
       *
       * - Baut ParameterDump auf Basis von Werten vom CMAES-Optimizer - Startet neue CC3D Simulation auf Basis der
       * Parameter - Wertet Ergebnisse aus dem CC3D Working Direktory für die Simulation aus - Gibt Ergebnis als
       * Fitnesswert an CMAES-Optimizer zurück - CMAES-Optimizer berechnet neu Werte - Prozess beginnt erneut.
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
            // "FitnessPlot.dat" .
            return 1.0;
         }

         /***
          * Loads actual fitnessPlot.dat from WorkingDirectory
          * and extracts it from file into a list
          *
          * @param currentSimulationWorkingDirectory
          * @return
          */
         private Collection<Double> getFitnessValuesFromDir(File currentSimulationWorkingDirectory) {
            File fitnessPlotFile = new File(currentSimulationWorkingDirectory, SimManagerConfig.FILENAMES_FITNESSPLOT);
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
            } catch (IOException e) {
               throw new RuntimeException(e.getMessage(), e);
            }
         }

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

      try {
         // ParameterDump mit Initialwerten füllen aus lokaler ParameterDump.dat
         // ParameterDump aus resources ordner laden
         double[] initialGuessValues = new double[] { modelAdhEnergy, modelAdhFactor, cellStemGrothVolumePerDay,
               cellStemNecrosisProb,
               cellStemnutrientRequirement, cellStemSurFit, cellStemVolFit, cellBasalGrothVolumePerDay,
               cellBasalNecrosisProb,
               cellBasalnutrientRequirement, cellBasalSurFit, cellBasalVolFit, cellIntermediateGrothVolumePerDay,
               cellIntermediateNecrosisProb, cellIntermediatenutrientRequirement, cellIntermediateSurFit,
               cellIntermediateVolFit, cellUmbrellaGrothVolumePerDay, cellUmbrellaNecrosisProb,
               cellUmbrellaNutrientRequirement, cellUmbrellaSurFit, cellUmbrellaVolFit };

         double[] inputSigmaDoubleArray = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
         CMAESOptimizer.Sigma sigma = new CMAESOptimizer.Sigma(inputSigmaDoubleArray);
         InitialGuess initialGuess = new InitialGuess(initialGuessValues);
         // population size: anzahl der zu optimierenden Werte?
         CMAESOptimizer.PopulationSize populationSize = new CMAESOptimizer.PopulationSize(6);
         GoalType goalTypeMaximize = GoalType.MAXIMIZE;

         // untergrenze und obergrenze der einzelnen Werte
         double[] lowerBounds = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
         double[] upperBounds = { 1000000000, 1000000000, 1000000000, 1000000000, 1000000000, 1000000000, 1000000000,
               1000000000, 1000000000, 1000000000, 1000000000, 1000000000, 1000000000, 1000000000, 1000000000,
               1000000000, 1000000000, 1000000000, 1000000000, 1000000000, 1000000000, 1000000000 };
         SimpleBounds simpleBounds = new SimpleBounds(lowerBounds, upperBounds);

         MaxEval maxEval = new MaxEval(10); // bestimmt Anzahl der fitnessfunktionaaufrufe

         PointValuePair optimize = cmaesOptimizer.optimize(internalFitnessFunction, populationSize,
               goalTypeMaximize, initialGuess, sigma, simpleBounds, maxEval);

         System.out.println("Done optimizing values");


         Assert.assertTrue(true);

      } finally {
         System.out.println("done with test");
      }
   }

   private ParameterDump getMockedParameterDump() throws IOException {
      File tmpParamDump = new File("tmp.dat");
      FileUtils.copyToFile(getClass().getClassLoader().getResourceAsStream("ParameterDump.dat"), tmpParamDump);
      ParameterDumpReader parameterDumpReader = new ParameterDumpReaderImpl();
      return parameterDumpReader.parseParamDump(tmpParamDump);
   }
}
