package cz.vondr.kiwi.algorithm.parallel;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.Solution;
import cz.vondr.kiwi.algorithm.Algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static cz.vondr.kiwi.algorithm.parallel.BruteForceWithInitState.NO_CITY;
import static java.util.Collections.synchronizedList;

public class ParallelManagerAlgorithm implements Algorithm {

    private BestSolutionHolder bestSolutionHolder = new BestSolutionHolder();

    private final List<BruteForceWithInitState> allAlgorithms = synchronizedList(new ArrayList<>());

    private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public Solution getBestSolution() {
        return bestSolutionHolder.get();
    }

    @Override
    public void init() {

    }

    @Override
    public void start() throws Exception {

        Runnable algorithmRunnable = () -> {
            BruteForceWithInitState bruteForceWithInitState = new BruteForceWithInitState(bestSolutionHolder);
            allAlgorithms.add(bruteForceWithInitState);

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
        };

        threadPool.execute(algorithmRunnable);



        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);


//        Thread algorithmThread = new Thread(algorithmRunnable);
//        algorithmThread.start();
//        algorithmThread.join();

    }

    @Override
    public void stop() {
        threadPool.shutdown();
        synchronized (allAlgorithms) {
            allAlgorithms.forEach(BruteForceWithInitState::stop);
        }
    }
}
