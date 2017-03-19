package cz.vondr.kiwi.algorithm.time_based;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;

import java.util.BitSet;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.lang.Integer.MAX_VALUE;

public class PQForTimeBased {

    private static final int QUEUE_INITIAL_CAPACITY = 5000;

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

//        1) delsi cesta
        int pathDiff = p2.path.length - p1.path.length;
        if (pathDiff != 0) {
            return pathDiff;
        }

        //TODO tohle neni moc supr, tady je potreba brat v potaz taky pristi cenu
        int flightsProcessedDiff = p1.flightsProcessed - p2.flightsProcessed;
        if (flightsProcessedDiff != 0) {
            return flightsProcessedDiff;
        }


//        2) lepsi cena
        return p1.price - p2.price;

    };

    //TODO tohle vyhodit, dobre jen pro ladeni.
    private long testedFlights = 0;

    private volatile boolean algorithmStopped = false;

    private short numberOfCities;

    private short finalDayIndex;

    //    private Solution bestSolution = new Solution(null, MAX_VALUE);
    private Progress bestProgress = new Progress(null, null, null, MAX_VALUE);

    private Data data;

    //if multiple threads access queue use PriorityBlockingQueue, but it will not be needed
    private Queue<Progress> queue = new PriorityQueue<>(QUEUE_INITIAL_CAPACITY, comparator);

    private void addToQueue(Progress p) {
        queue.add(p);
        testedFlights++;
    }

    public Progress getBestProgress() {
        return bestProgress;
    }

    public PQForTimeBased setBestProgress(Progress bestProgress) {
        this.bestProgress = bestProgress;
        return this;
    }

    public PQForTimeBased init(Progress initialProgress, short finalDayIndex) {
        this.data = Salesman.data;
        numberOfCities = data.numberOfCities;

        addToQueue(initialProgress);
        this.finalDayIndex = finalDayIndex;
        return this;
    }


    public PQForTimeBased start() {
//        //Initial state
//        Progress p1 = new Progress(new short[0], new BitSet(numberOfCities), 0);
//        addToQueue(p1);

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

            int nextPrice = p.price + flight.price;

            if (actualDay >= finalDayIndex) {
//                if (nextCity == data.startCity) {
                if (nextPrice < bestProgress.price) {
//                            short[] pathCopy = Arrays.copyOf(actualPath, actualPath.length);
                    bestProgress = createNextProgress(p, nextCity, nextPrice);
                }
                //                    logger.info("Solution found. Price = " + nextPrice + ", path=" + Arrays.toString(actualPath) + ", testedFlights="+testedFlights);
//                }
                continue;
            }

            if (nextPrice >= bestProgress.price) { // very simple throw too expensive paths TODO do it even better
                continue;
            }

            Progress nextProgress = createNextProgress(p, nextCity, nextPrice);
//                logger.info("Fly from " + actualCity + " to " + nextCity);
            addToQueue(nextProgress);


        }

        queue.clear();//clear or not to clear - omg its too late today - fresh is not my name right now :)



        return this;
    }

    private Progress createNextProgress(Progress p, short nextCity, int nextPrice) {
        short[] nextPath = new short[p.path.length + 1];
        System.arraycopy(p.path, 0, nextPath, 0, p.path.length);
        nextPath[nextPath.length - 1] = nextCity;
        BitSet nextVisitedCities = (BitSet) p.visitedCities.clone();
        nextVisitedCities.set(nextCity);

        int[] nextPrices = new int[p.prices.length + 1];
        System.arraycopy(p.prices, 0, nextPrices, 0, p.prices.length);
        nextPrices[nextPrices.length - 1] = nextPrice;
        return new Progress(nextPath, nextPrices, nextVisitedCities, nextPrice);
    }

    public void stop() {
        algorithmStopped = true;
    }


}
