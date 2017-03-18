package cz.vondr.kiwi.algorithm.parallel;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.Solution;
import cz.vondr.kiwi.algorithm.Algorithm;
import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static cz.vondr.kiwi.algorithm.parallel.BruteForceWithInitState.NO_CITY;
import static java.util.Collections.synchronizedList;

public class ParallelManagerAlgorithm implements Algorithm {

    private BestSolutionHolder bestSolutionHolder = new BestSolutionHolder();

    private final List<BruteForceWithInitState> activeOrFinishedAlgorithm = synchronizedList(new ArrayList<>());

    //    private ExecutorService threadPool;
    private ExecutorService threadPool;

    public Solution getBestSolution() {
        return bestSolutionHolder.get();
    }

    @Override
    public void init() {
//        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        threadPool = Executors.newFixedThreadPool(20);
    }

    @Override
    public void start() throws Exception {

        Day day = Salesman.data.days[0];
        City city = day.cities[0];
        for (int actualFlight = 0; actualFlight < city.flights.length; actualFlight++) {
            Flight flight = city.flights[actualFlight];


            Runnable algorithmRunnable = () -> {
                BruteForceWithInitState bruteForceWithInitState = new BruteForceWithInitState(bestSolutionHolder);
                activeOrFinishedAlgorithm.add(bruteForceWithInitState);

                short[] actualPath = createEmptyPath();
                actualPath[0] = flight.destination;

                short actualDayIndex = 1;


//            //Test pro Data 10
//            actualPath[0] = 3;
//            actualPath[1] = 9;
//            actualDayIndex = 2;
//            bruteForceWithInitState.init(actualPath, actualDayIndex, (short) 0, (short)4, 908);
                bruteForceWithInitState.init(actualPath, actualDayIndex, (short) 0, (short) 5000, flight.price);

                try {
                    bruteForceWithInitState.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

            threadPool.execute(algorithmRunnable);

        }

        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);


//        Thread algorithmThread = new Thread(algorithmRunnable);
//        algorithmThread.start();
//        algorithmThread.join();

    }

    private short[] createEmptyPath() {
        short numberOfCities = Salesman.cityNameMapper.getNumberOfCities();
        short[] actualPath = new short[numberOfCities - 1];
        for (int i = 0; i < actualPath.length; i++) {
            actualPath[i] = NO_CITY;
        }
        return actualPath;
    }

    @Override
    public void stop() {

        threadPool.shutdownNow();
        synchronized (activeOrFinishedAlgorithm) {
            activeOrFinishedAlgorithm.forEach(BruteForceWithInitState::stop);
        }
    }
}
