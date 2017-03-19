package cz.vondr.kiwi.algorithm.time_based;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.Solution;
import cz.vondr.kiwi.StopWatch;
import cz.vondr.kiwi.algorithm.Algorithm;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.solutionwriter.SolutionWriter;

import java.util.Arrays;
import java.util.BitSet;

import static java.lang.Integer.MAX_VALUE;

public class TimeBasedAlgorithm implements Algorithm {


    private Solution bestSolution = new Solution(null, MAX_VALUE);

    private Data data;

    private long algorithmStartTime;
    private long timeForAlgorithm;
    private StopWatch algorithmActualTime;

    @Override
    public Solution getBestSolution() {
        return bestSolution;
    }

    @Override
    public void init() {
        this.data = Salesman.data;
    }

    @Override
    public void start() {
        computeAlgorithmTimes();


        Progress initialProgress = new Progress(new short[0], new int[0], new BitSet(data.numberOfCities), 0);
//        PQForTimeBased al1 = new PQForTimeBased()
//                .init(initialProgress, (short) 5)
//                .start();
//
//
//        PQForTimeBased al2 = new PQForTimeBased()
//                .init(al1.getBestProgress(), (short) 9)
//                .start();
//
//        Progress bestProgress = al2.getBestProgress();

        short actualFinalIndex = 0;
        PQForTimeBased actualAlg = new PQForTimeBased().setBestProgress(initialProgress);
        while (actualFinalIndex < data.numberOfCities - 1) {
            int increment = 8;
            actualFinalIndex += increment;
            Progress nextProgress;
            if (actualFinalIndex == increment) {
                actualFinalIndex--;
                nextProgress = actualAlg.getBestProgress();
            } else {
                Progress bestProgress = actualAlg.getBestProgress();
                nextProgress = createShorterProgress(bestProgress, (short) (bestProgress.getDayIndex() - (increment/2)));
                actualFinalIndex -= increment/2;
            }



            actualAlg = new PQForTimeBased()
                    .init(nextProgress, actualFinalIndex)
                    .start();

        }


        Progress bestProgress = actualAlg.getBestProgress();

        bestSolution = new Solution(
                Arrays.copyOf(bestProgress.path, bestProgress.path.length - 1),
                bestProgress.price
        );

        try {
            new SolutionWriter(data, bestSolution).writeSolutionToLog();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Progress createShorterProgress(Progress p, short newFinalIndex) {


        short[] newPath = Arrays.copyOf(p.path, newFinalIndex);
        int[] newPrices = Arrays.copyOf(p.prices, newFinalIndex);

        BitSet newBitset = new BitSet();
        for (int i = 0; i < newPath.length; i++) {
            newBitset.set(newPath[i]);
        }


        return new Progress(
                newPath,
                newPrices,
                newBitset,
                newPrices[newPrices.length - 1]
        );
    }


    private void computeAlgorithmTimes() {
        algorithmActualTime = new StopWatch();
        algorithmStartTime = Salesman.actualTime.splitTime();
        timeForAlgorithm = Salesman.TOTAL_ALGORITHM_TIME - algorithmStartTime;
        timeForAlgorithm -= 1000; //ubrat vterinu jen tak pro jistotu
    }


    @Override
    public void stop() {
    }


}
