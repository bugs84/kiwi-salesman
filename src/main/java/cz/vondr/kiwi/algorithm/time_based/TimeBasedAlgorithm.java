package cz.vondr.kiwi.algorithm.time_based;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.Solution;
import cz.vondr.kiwi.StopWatch;
import cz.vondr.kiwi.algorithm.Algorithm;
import cz.vondr.kiwi.data.Data;

import static java.lang.Integer.MAX_VALUE;

public class TimeBasedAlgorithm implements Algorithm {

    private Solution bestSolution = new Solution(null, MAX_VALUE);

    private Data data;

    private long algorithmStartTime;
    private long timeForAlgorithm;
    private StopWatch algorithmActualTime;

    @Override
    public Solution getBestSolution() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void init() {
        this.data = Salesman.data;
    }

    @Override
    public void start() {
        computeAlgorithmTimes();



    }

    private void computeAlgorithmTimes() {
        algorithmActualTime = new StopWatch();
        algorithmStartTime = Salesman.actualTime.splitTime();
        timeForAlgorithm = Salesman.TOTAL_ALGORITHM_TIME - algorithmStartTime;
        timeForAlgorithm -= 1000; //ubrat vterinu jen tak pro jistotu
    }


    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not implemented");
    }


}
