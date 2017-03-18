package cz.vondr.kiwi.algorithm.parallel;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.Solution;
import cz.vondr.kiwi.algorithm.Algorithm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cz.vondr.kiwi.algorithm.parallel.BruteForceWithInitState.NO_CITY;

public class ParallelManagerAlgorithm implements Algorithm {

    private BestSolution bestSolution = new BestSolution();


    @Override
    public Solution getBestSolution() {
        return bestSolution.getBestSolution();
    }

    @Override
    public void init() {

    }

    @Override
    public void start() throws Exception {

        BruteForceWithInitState bruteForceWithInitState = new BruteForceWithInitState();

        Thread algorithmThread = new Thread(() -> {

            short numberOfCities = Salesman.cityNameMapper.getNumberOfCities();
            short[] actualPath = new short[numberOfCities - 1];
            for (int i = 0; i < actualPath.length; i++) {
                actualPath[i] = NO_CITY;
            }

            short actualDayIndex = 0;


//            //Test pro Data 10
//            actualPath[0] = 3;
//            actualPath[1] = 9;
//            actualDayIndex = 2;
//            bruteForceWithInitState.init(actualPath, actualDayIndex, (short) 0, (short)4, 908);
            bruteForceWithInitState.init(actualPath, actualDayIndex, (short) 0, (short) 5000, 0);

            try {
                bruteForceWithInitState.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            bestSolution.setBestSolution(bruteForceWithInitState.getBestSolution());
        });
        algorithmThread.start();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            bestSolution.setBestSolution(bruteForceWithInitState.getBestSolution());
        }, 100, 500, TimeUnit.MILLISECONDS);

        algorithmThread.join();
    }

    @Override
    public void stop() {

    }
}
