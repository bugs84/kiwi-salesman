package cz.vondr.kiwi.algorithm.parallel_pq;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.Solution;
import cz.vondr.kiwi.algorithm.Algorithm;
import cz.vondr.kiwi.algorithm.parallel.BestSolutionHolder;
import cz.vondr.kiwi.algorithm.pq.Progress;
import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cz.vondr.kiwi.algorithm.parallel_pq.BruteForceWithInitState.NO_CITY;
import static java.util.Collections.synchronizedList;

public class PqParallelManagerAlgorithm implements Algorithm {

    private final static Logger logger = LoggerFactory.getLogger(PqParallelManagerAlgorithm.class);

    private BestSolutionHolder bestSolutionHolder = new BestSolutionHolder();

    private final List<BruteForceWithInitState> activeOrFinishedAlgorithm = synchronizedList(new ArrayList<>());

    //    private ExecutorService threadPool;
    private ThreadPoolExecutor threadPool;

    public Solution getBestSolution() {
        return bestSolutionHolder.get();
    }

    @Override
    public void init() {
//        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
        initPq();
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
        stopPq();
        threadPool.getQueue().clear();
        threadPool.shutdownNow();
        synchronized (activeOrFinishedAlgorithm) {
            activeOrFinishedAlgorithm.forEach(BruteForceWithInitState::stop);
        }
    }


    private static final int QUEUE_INITIAL_CAPACITY = 5000;

    private final Comparator<Progress> comparator = (p1, p2) -> {
        //TODO asi se da jeste poladit - ale zalezi kudy chceme chodit

        int priorityDiff = p1.priorityPenalty - p2.priorityPenalty;
        if (priorityDiff != 0) {
            return priorityDiff;
        }

        int flightsProcessedDiff = p1.flightsProcessed - p2.flightsProcessed;
        if (flightsProcessedDiff != 0) {
            return flightsProcessedDiff;
        }

        //        1a) delsi cesta (do hloubky)
        int pathDiff = p2.path.length - p1.path.length;


//                1b) do sirky
//        int pathDiff = p1.path.length - p2.path.length;
        if (pathDiff != 0) {
            return pathDiff;
        }




        //        2) lepsi cena
        return p1.price - p2.price;

    };

    //TODO tohle vyhodit, dobre jen pro ladeni.
    private long testedFlights = 0;

    private volatile boolean algorithmStopped = false;

    private short numberOfCities;

//        private Solution bestSolution = new Solution(null, MAX_VALUE);

    private Data data;

    //if multiple threads access queue use PriorityBlockingQueue, but it will not be needed
    private Queue<Progress> queue = new PriorityQueue<>(QUEUE_INITIAL_CAPACITY, comparator);


    private void addToQueue(Progress p) {
        queue.add(p);
        testedFlights++;
    }

    public void initPq() {
        this.data = Salesman.data;
        numberOfCities = data.numberOfCities;
    }

    @Override
    public void start() throws Exception {

        addInitialBruteForceForAll();


        //Initial state
        Progress p1 = new Progress(new short[0], new BitSet(numberOfCities), 0);
        //        p1.flightsProcessed = 1;
        addToQueue(p1);

        Progress p;
        while ((p = queue.poll()) != null) {
            if (algorithmStopped) {
                break;
            }

            short actualDay = p.getDayIndex();
            short actualCity = p.getActualCity();
            Day day = data.days[actualDay];
            City city = day.cities[actualCity];


            //Remove and insert
            short actualFlight = p.flightsProcessed;
            Flight flight = city.flights[actualFlight];


            if (p.flightsProcessed > 0) { //na tohle uz jede
                addBruteForceThread(p);
            }


            int actualPriorityPenalty = p.priorityPenalty;
            Progress newP = p.copy();
            newP.flightsProcessed++;
            newP.priorityPenalty++;
            if (newP.flightsProcessed < city.flights.length) {
                addToQueue(newP);
            }

            //process deeper path


            short nextCity = flight.destination;

            //do not flight, where you already been  ||  flight to start, before last day
            if (p.visitedCities.get(nextCity) || (nextCity == data.startCity && !(actualDay >= numberOfCities - 1))) {
                continue;
            }

            //TODO  NEPOUSTET dalsi thread, kdyz je do konce mene nez cca 15 mest
//            NEPOUSTET dalsi thread, kdyz je do konce mene nez cca 15 mest
            if ((numberOfCities - 1 - actualCity) < 15) {
                continue;
            }


            //            short nextDay = (short) (actualDay + 1);
            int nextPrice = p.price + flight.price;

            if (actualDay >= numberOfCities - 1) {
//                if (nextCity == data.startCity) {
//                    if (nextPrice < bestSolution.price) {
//                        //                            short[] pathCopy = Arrays.copyOf(actualPath, actualPath.length);
//                        bestSolution = new Solution(p.path, nextPrice);
//                        logger.info("New Best solution found. Price = " + nextPrice + ", path=" + Arrays.toString(bestSolution.path) + ", testedFlights=" + testedFlights);
//                    }
//                    //                    logger.info("Solution found. Price = " + nextPrice + ", path=" + Arrays.toString(actualPath) + ", testedFlights="+testedFlights);
//                }
                continue;
            }

            if (nextPrice >= bestSolutionHolder.get().price) { // very simple throw too expensive paths TODO do it even better
                continue;
            }

            short[] nextPath = new short[p.path.length + 1];
            System.arraycopy(p.path, 0, nextPath, 0, p.path.length);
            nextPath[nextPath.length - 1] = nextCity;
            BitSet nextVisitedCities = (BitSet) p.visitedCities.clone();
            nextVisitedCities.set(nextCity);
            //                logger.info("Fly from " + actualCity + " to " + nextCity);
            int nextPriorityPenalty = actualPriorityPenalty + actualFlight + 1;
            addToQueue(new Progress(nextPath, nextVisitedCities, nextPrice, nextPriorityPenalty));


            //Zpomal pokud je fronta moc plna
            //TODO vychytat zde konstanty
            while (threadPool.getQueue().size() > 1000) {
                   Thread.sleep(50);
            }

        }


        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);

        logger.info("Algorithm ended - TestedFlights=" + testedFlights);

    }

    private void addInitialBruteForceForAll() {
        Runnable algorithmRunnable = () -> {
            try {
                BruteForceWithInitState bruteForceWithInitState = new BruteForceWithInitState(bestSolutionHolder);
                activeOrFinishedAlgorithm.add(bruteForceWithInitState);

                short[] actualPath = createEmptyPath();

                short actualDayIndex = 0;

                bruteForceWithInitState.init(actualPath, actualDayIndex, (short) 0, (short) 9999/*process all flights*/, 0);

                bruteForceWithInitState.start();
            } catch (Exception e) {
                //this should never happen, but who knows...  Return at least something...
                logger.error("Initial Algorithm Failed!!!!", e);
                //                    throw new RuntimeException(e);
            }
        };

        threadPool.execute(algorithmRunnable);
    }

    private void addBruteForceThread(Progress p) {

        Runnable algorithmRunnable = () -> {
            try {
                BruteForceWithInitState bruteForceWithInitState = new BruteForceWithInitState(bestSolutionHolder);
                activeOrFinishedAlgorithm.add(bruteForceWithInitState);

                short[] actualPath = createEmptyPath();
                System.arraycopy(p.path, 0, actualPath, 0, p.path.length);
//                actualPath[0] = flight.destination;

                short actualDayIndex = p.getDayIndex();


                //            //Test pro Data 10
                //            actualPath[0] = 3;
                //            actualPath[1] = 9;
                //            actualDayIndex = 2;
                //            bruteForceWithInitState.init(actualPath, actualDayIndex, (short) 0, (short)4, 908);
                bruteForceWithInitState.init(actualPath, actualDayIndex, p.flightsProcessed, p.flightsProcessed, p.price);


                logger.info("Start PQ - " + Arrays.toString(p.path) + " - flight=" + p.flightsProcessed + ",  pPenalty="+ p.priorityPenalty + ",  price=" + p.price);
                bruteForceWithInitState.start();
            } catch (Exception e) {
                //this should never happen, but who knows...  Return at least something...
                logger.error("Successive Algorithm Failed!!!!", e);
                //                    throw new RuntimeException(e);
            }
        };

//        logger.info("Add   PQ - " + Arrays.toString(p.path) + " - flight=" + p.flightsProcessed + ",  pPenalty="+ p.priorityPenalty + ",  price=" + p.price);
        threadPool.execute(algorithmRunnable);


    }

    public void stopPq() {
        algorithmStopped = true;
    }

}
