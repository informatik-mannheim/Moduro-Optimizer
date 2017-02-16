package hs.mannheim.moduro.automation.cc3d.simulation.manager.optimization;

import hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model.ParameterDump;

import java.io.File;

/***
 * This is a mock implementation of a parameterOptimizer.
 * It will only return the originParameterDump so no optimization happens.
 */
public class MockParameterOptimizerImpl implements ParameterOptimizer {

    private static final String OPTIMIZER_MOCK_NAME = "mock";

    public ParameterDump optimizeValues(ParameterDump originParameterDump) {
        try {
            System.out.println("Enter optimizeValues()");
            System.out.println("This optimization method is a Mock-Implementation. " +
                    "It will just return the origin parameterDump.");

            // usually this is where we want to optimize values of the parameterDump
            // since compucell3d doesn't accept negative values
            return originParameterDump;
        } finally {
            System.out.println("Exit optimizeValues()");
        }
    }

    @Override
    public Double getInternalFitnessOfLatestCC3DSimulation(File cc3DWorkingDir) {
        return 0.0d;
    }

    @Override
    public String getOptimizerName() {
        return OPTIMIZER_MOCK_NAME;
    }
}
