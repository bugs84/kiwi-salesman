package cz.vondr.kiwi.algorithm.pq;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.algorithm.simple.SimpleBruteForceAlgorithm;
import cz.vondr.kiwi.algorithm.simple.Solution;
import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.lang.Integer.MAX_VALUE;

public class PriorityQueueAlgorithm {
    private final static Logger logger = LoggerFactory.getLogger(SimpleBruteForceAlgorithm.class);

    private static final int QUEUE_INITIAL_CAPACITY = 5000;

    private static final Comparator<Progress> comparator = (p1, p2) -> {
//        1) delsi cesta
        int pathDiff = p2.path.length - p1.path.length;
//        2) lepsi cena
        return pathDiff == 0 ? (p1.price - p2.price) : pathDiff;

    };

    //TODO tohle vyhodit, dobre jen pro ladeni.
    private long testedFlights = 0;

    private short numberOfCities = Salesman.cityNameMapper.getNumberOfCities();

    private Solution bestSolution = new Solution(null, MAX_VALUE);

    private Data data;

    //if multiple threads access queue use PriorityBlockingQueue, but it will not be needed
    private Queue<Progress> queue = new PriorityQueue<>(QUEUE_INITIAL_CAPACITY, comparator);

    private void addToQueue(Progress p) {
        queue.add(p);
        testedFlights++;
    }

    public Solution getBestSolution() {
        return bestSolution;
    }


    public PriorityQueueAlgorithm(Data data) {
        this.data = data;
    }

    public void start() {
        //Initial state                       /** bitset 0 = vsechno false */
        addToQueue(new Progress(new short[0], new BitSet(numberOfCities), 0));

        Progress p;
        while ((p = queue.poll()) != null) {

            short actualDay = p.getDayIndex();
            short actualCity = p.getActualCity();
            Day day = data.days[actualDay];
            City city = day.cities[actualCity];

            for (int actualFlight = 0; actualFlight < city.flights.length; actualFlight++) {
                Flight flight = city.flights[actualFlight];

                short nextCity = flight.destination;

                //do not flight, where you already been  ||  flight to start, before last day
                if (p.visitedCities.get(nextCity) || (nextCity == data.startCity && !(actualDay >= numberOfCities - 1))) {
                    continue;
                }

                short nextDay = (short) (actualDay + 1);
                int nextPrice = p.price + flight.price;

                if (actualDay >= numberOfCities - 1) {
                    if (nextCity == data.startCity) {
                        if (nextPrice < bestSolution.price) {
//                            short[] pathCopy = Arrays.copyOf(actualPath, actualPath.length);
                            bestSolution = new Solution(p.path, nextPrice);
                            logger.info("New Best solution found. Price = " + nextPrice + ", path=" + Arrays.toString(bestSolution.path) + ", testedFlights=" + testedFlights);
                        }
                        //                    logger.info("Solution found. Price = " + nextPrice + ", path=" + Arrays.toString(actualPath) + ", testedFlights="+testedFlights);
                    }
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

        }


    }


}
