package cz.vondr.kiwi.algorithm.progressiveDepthPriorityAlgorithm;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.Solution;
import cz.vondr.kiwi.algorithm.Algorithm;
import cz.vondr.kiwi.algorithm.pq.Progress;
import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;

import java.util.BitSet;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.MAX_VALUE;

public class ProgressiveDepthPriorityQueueAlgorithm implements Algorithm {

    private static final int QUEUE_INITIAL_CAPACITY = 5000;

    private short actualMaxDayIndex = 0;

    private final Comparator<Progress> comparator = (p1, p2) -> {
//        if (p2.path.length < 3 || p1.path.length < 3) {
//            return p1.path.length - p2.path.length;
//        }

////        if (bestSolution.price != MAX_VALUE) {
//            int flightsProcessedDiff = p1.flightsProcessed - p2.flightsProcessed;
//            if (flightsProcessedDiff != 0) {
//                return flightsProcessedDiff;
//            }
////        }
        short p1DayIndex = p1.getDayIndex();
        short p2DayIndex = p2.getDayIndex();
        if ((p1DayIndex < actualMaxDayIndex && p2DayIndex < actualMaxDayIndex) ||
                (p1DayIndex >= actualMaxDayIndex && p2DayIndex >= actualMaxDayIndex)
                ) {

            //tohle kdyz jsou obe kratsi nez actualMaxDayIndex
            //nebo obe delsi

//        1) delsi cesta
            int pathDiff = p2.path.length - p1.path.length;
            if (pathDiff != 0) {
                return pathDiff;
            }


            //TODO tohle ber podle nejlepsi dalsi ceny! (musi se pridat do progressu)
//        int flightsProcessedDiff = p1.flightsProcessed - p2.flightsProcessed;
//        if (flightsProcessedDiff != 0) {
//            return flightsProcessedDiff;
//        }

//        2) lepsi cena
            return p1.price - p2.price;

        } else {

            //kdyz jedna znich prekroci max index, tak ber tu kratsi
            return p1DayIndex - p2DayIndex;
        }

    };

    //TODO tohle vyhodit, dobre jen pro ladeni.
    private long testedFlights = 0;

    private volatile boolean algorithmStopped = false;

    private short numberOfCities;

    private Solution bestSolution = new Solution(null, MAX_VALUE);

    private Data data;

    //if multiple threads access queue use PriorityBlockingQueue, but it will not be needed
    private Queue<Progress> queue = new PriorityQueue<>(QUEUE_INITIAL_CAPACITY, comparator);

    private void addToQueue(Progress p) {
        if(actualMaxDayIndex!=lastMaxDayIndex) {
            //reorderQueue
            lastMaxDayIndex = actualMaxDayIndex;
                               //??? not optimal
            PriorityQueue<Progress> newQueue = new PriorityQueue<>((queue.size()+1) * 2, comparator);
            newQueue.addAll(queue);
            queue = newQueue;
        }


        queue.add(p);
        testedFlights++;
    }

    @Override
    public Solution getBestSolution() {
        return bestSolution;
    }


    @Override
    public void init() {
        this.data = Salesman.data;
        numberOfCities = data.numberOfCities;
    }

    short lastMaxDayIndex = 0;

    @Override
    public void start() {
        //konstanty optimalizovany pro 100 data set
        actualMaxDayIndex=1;
        lastMaxDayIndex = actualMaxDayIndex;
        new Timer("actualMaxDayIndexIncreasor").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                actualMaxDayIndex += 1;
            }
        }, 0L, 200L);

        startInternal();
    }

    private void startInternal() {
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
            p.flightsProcessed++;
            if (p.flightsProcessed < city.flights.length) {
                addToQueue(p);
            }

            //process deeper path
            Flight flight = city.flights[actualFlight];

            short nextCity = flight.destination;

            //do not flight, where you already been  ||  flight to start, before last day
            if (p.visitedCities.get(nextCity) || (nextCity == data.startCity && !(actualDay >= numberOfCities - 1))) {
                continue;
            }

//            short nextDay = (short) (actualDay + 1);
            int nextPrice = p.price + flight.price;

            if (actualDay >= numberOfCities - 1) {
                if (nextCity == data.startCity) {
                    if (nextPrice < bestSolution.price) {
//                            short[] pathCopy = Arrays.copyOf(actualPath, actualPath.length);
                        bestSolution = new Solution(p.path, nextPrice);
                    }
                    //                    logger.info("Solution found. Price = " + nextPrice + ", path=" + Arrays.toString(actualPath) + ", testedFlights="+testedFlights);
                }
                continue;
            }

            if (nextPrice >= bestSolution.price) { // very simple throw too expensive paths TODO do it even better
                continue;
            }

            short[] nextPath = new short[p.path.length + 1];
            System.arraycopy(p.path, 0, nextPath, 0, p.path.length);
            nextPath[nextPath.length - 1] = nextCity;
            BitSet nextVisitedCities = (BitSet) p.visitedCities.clone();
            nextVisitedCities.set(nextCity);
//                logger.info("Fly from " + actualCity + " to " + nextCity);
            addToQueue(new Progress(nextPath, nextVisitedCities, nextPrice));


        }

        queue.clear();//clear or not to clear - omg its too late today - fresh is not my name right now :)



    }

    @Override
    public void stop() {
        algorithmStopped = true;
    }


}
