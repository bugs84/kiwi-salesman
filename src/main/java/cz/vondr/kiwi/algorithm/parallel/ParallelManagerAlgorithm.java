package cz.vondr.kiwi.algorithm.parallel;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.Solution;
import cz.vondr.kiwi.algorithm.Algorithm;

import static cz.vondr.kiwi.algorithm.parallel.BruteForceWithInitState.NO_CITY;

public class ParallelManagerAlgorithm implements Algorithm {

    private BestSolutionHolder bestSolutionHolder = new BestSolutionHolder();

    public Solution getBestSolution() {
        return bestSolutionHolder.get();
    }

    @Override
    public void init() {

    }

    @Override
    public void start() throws Exception {

        BruteForceWithInitState bruteForceWithInitState = new BruteForceWithInitState(bestSolutionHolder);

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
        });
        algorithmThread.start();

        algorithmThread.join();
    }

    @Override
    public void stop() {

    }
}
