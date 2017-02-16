package cmaes;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class InternalFitnessFunctionTest {

    @Test
    public void testInternalFitness() {


        Collection<Double> fitnessPlotResultCollection = Arrays.asList(0.350741850649,
                0.392026550408,
                0.448965503545,
                0.5088996469,
                0.503545882929,
                0.508833084299,
                0.513155578365,
                0.518349911646,
                0.510039158593,
                0.508198396365,
                0.515317216501,
                0.516081810964,
                0.524758354435,
                0.52590642937,
                0.529547451566,
                0.538795345719,
                0.549375354977,
                0.547995736311,
                0.542677027325,
                0.546175594157,
                0.549633220485,
                0.554454339992,
                0.572376085927,
                0.575001174011,
                0.581660252034,
                0.585406541779,
                0.596205117626,
                0.606141145505,
                0.610390062665,
                0.614703271197,
                0.608476260304,
                0.598558183877,
                0.605826287027,
                0.606260263582,
                0.616028845202,
                0.620585238453,
                0.629054226741,
                0.639747904307,
                0.635213722416,
                0.644766529283,
                0.655618983934,
                0.669429937662,
                0.664218091467,
                0.654396835841,
                0.661450078933,
                0.661491151185,
                0.655439913963,
                0.663274319412,
                0.661196707561,
                0.65907817849,
                0.666838259882,
                0.674590167289,
                0.683176209385,
                0.688321436576,
                0.693359127314,
                0.692373965855
        );

        /**
         * was will ich tun?
         * Als result bekommt man einen Skalar mit einem ParameterDump und einer dazugehörigen Fitness.
         * Diese Fitness liegt in einem Intervall zwischen [0,1]. Diese Fitness setzt sich zusammen aus dem Mittelwert
         * aller Fitness-Werte zum Zeitpunkt [t], also zu jedem mcs. Eine Simulation dauert 720 Tage und jeden Tag werden 2 mcs
         * vollzogen.
         *
         * Das Ergebnis der internen Fitness ist also der Mittelwert der gesamten Simulation. Dazu werden alle mcs addiert und durch die Anzahl
         * der vorhandenen Einträge (Anzahl mcs) dividiert. Das Ergebnis liefert immer einen korrekten Mittelwert.
         * Deshalb sollte jeder Plot-Eintrag lediglich auf die Anzahl der mcs überprüft werden, um auf das Beenden einer Simulation zu prüfen.
         * Sollte dies nicht der Fall sein, ist die Simulation abgebrochen und ein negativer return-Wert wird zurückgegeben.
         */
        final Integer MINIMUM_COUNT = 10;

        final Double MEDIAN_FITNESS_MINIMUM_VALUE = 0.75;

        final Double LAST_ELEMENT_MINIMUM_VALUE = 0.55;

        if (fitnessPlotResultCollection.size() < MINIMUM_COUNT) {
            System.out.println("Minimum Count not reached. Item count: " + fitnessPlotResultCollection.size());
            //fitness not reached
            // return 0.0
        }

        // Median
        Double fitnessMedian = 0.0;
        for (Double collectionElement : fitnessPlotResultCollection) {
            fitnessMedian += collectionElement.doubleValue();
        }
        if ((fitnessMedian = (fitnessMedian / fitnessPlotResultCollection.size())) < MEDIAN_FITNESS_MINIMUM_VALUE) {
            System.out.println("MedianFitness is: " + fitnessMedian);
            System.out.println("MedianFitness not reached at: " + fitnessMedian);
        } else {
            System.out.println("MedianFitness is: " + fitnessMedian);
            System.out.println("CollectionSize is: " + fitnessPlotResultCollection.size());
        }

        //Last Element and min/max
        final Iterator collectionIterator = fitnessPlotResultCollection.iterator();
        Object lastElement = collectionIterator.next();
        while (collectionIterator.hasNext()) {
            lastElement = collectionIterator.next();
            Double minMaxValue = (Double) lastElement;
            if (minMaxValue < 0.0 || minMaxValue > 1.0) {
                System.out.println("Element is out of range");
                //fitness not reached
            }
        }
        Double lastElementValue = (Double) lastElement;
        System.out.println();
        if (lastElementValue <= LAST_ELEMENT_MINIMUM_VALUE) {
            System.out.println(lastElement + " as last Collection element is too small");
            //fitness not reached
        } else {
            System.out.println(lastElement + " as last collection element is ok");
        }

        Assert.assertTrue(true);
    }


    // TODO: IMPL
    private double fitnessFunct(Collection<Double> valueCollection) {
        Double internalFitnessResult = null;

        if (valueCollection == null || valueCollection.size() == 0) {
            System.err.println("No FitnessPlot.dat values retrieved");
            return 0.0;
        }

        Double highestValDouble = null;
        for (Double fitnessValue : valueCollection) {
            highestValDouble = highestValDouble == null || highestValDouble < fitnessValue ? fitnessValue : highestValDouble;
        }
        System.out.println("HighestValue: " + highestValDouble);

        Double lowestValue = null;
        for (Double fitnessValue : valueCollection) {
            lowestValue = lowestValue == null || lowestValue > fitnessValue ? fitnessValue : lowestValue;
        }
        System.out.println("LowestValue: " + lowestValue);

        //brauch ich nicht
        Double maxDiff = highestValDouble - lowestValue;

        ArrayList<Double> valuesArrayList = Lists.newArrayList(valueCollection);
        Double unsortedMedianValue = valuesArrayList.get(valuesArrayList.size() / 2);
        // todo: liste sortieren, unsorted median wegwerfen - Sortieren in eigener methode -> eher nicht

        // todo: was tun, wenn mehr als 720 Werte in der Liste stehen?
        Double OPTIMUM_STEPS_FACTOR = 1.0;
        Double OPTIMUM_STEPS_SIZE = 720.0; // Fachlich sind 720 Einträge in der FitnessPlot.dat das Maximum
        Double initCountFitnessValue = valueCollection.size() / OPTIMUM_STEPS_SIZE;

        return internalFitnessResult * initCountFitnessValue * OPTIMUM_STEPS_FACTOR;
    }
}
