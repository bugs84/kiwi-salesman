package cz.vondr.kiwi.algorithm.parallel;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.Solution;
import cz.vondr.kiwi.algorithm.Algorithm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cz.vondr.kiwi.algorithm.parallel.BruteForceWithInitState.NO_CITY;
import static java.lang.Integer.MAX_VALUE;

public class ParallelManagerAlgorithm implements Algorithm {

    private Solution bestSolution = new Solution(null, MAX_VALUE);

    @Override
    public Solution getBestSolution() {
        return bestSolution;
    }

    @Override
    public void init() {

    }

    @Override
    public void start() throws InterruptedException {

        BruteForceWithInitState bruteForceWithInitState = new BruteForceWithInitState();

        Thread algorithmThread = new Thread(() -> {

            short numberOfCities = Salesman.cityNameMapper.getNumberOfCities();
            short[] actualPath = new short[numberOfCities - 1];
            for (
                    int i = 0;
                    i < actualPath.length; i++)

            {
                actualPath[i] = NO_CITY;
            }

            short actualDayIndex = 0;


            bruteForceWithInitState.init(actualPath, actualDayIndex, (short) 0, (short) 1000);

            bruteForceWithInitState.start();
            bestSolution = bruteForceWithInitState.getBestSolution();
        });
        algorithmThread.start();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() ->{
            bestSolution = bruteForceWithInitState.getBestSolution();
        }, 100, 500, TimeUnit.MILLISECONDS);

        algorithmThread.join();
    }

    @Override
    public void stop() {

    }
}
