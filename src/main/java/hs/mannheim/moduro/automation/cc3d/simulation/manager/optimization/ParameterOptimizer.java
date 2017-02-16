package hs.mannheim.moduro.automation.cc3d.simulation.manager.optimization;

import java.io.File;

/***
 * This interface is used to provide optimization algorithms.
 * If you want to add another algorithm to optimize, please do not change any of the existing implementations.
 * Instead, create a new class in the optimization packe which implements this interface.
 *
 * IMPORTANT:
 * Do not forget to register your new class in the Main method or it will not be available for usage.
 * (See method registerParameterOptimizers in mainClass)
 *
 * Please notice - Compucell doesn't accept negative values.
 * (Well - it does, but the programm will either crash or stop after 2 seconds)
 * This might be affect your optimization algorithm or the results you will get from it.
 */
public interface ParameterOptimizer {


    Double getInternalFitnessOfLatestCC3DSimulation(File cc3DWorkingDir);
}
