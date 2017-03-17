package cz.vondr.kiwi.algorithm.simple;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.Solution;
import cz.vondr.kiwi.algorithm.Algorithm;
import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.BitSet;

import static java.lang.Integer.MAX_VALUE;

public class SimpleOptimalizedBruteForceAlgorithm implements Algorithm {

    private final static Logger logger = LoggerFactory.getLogger(SimpleOptimalizedBruteForceAlgorithm.class);
    private static final int NO_CITY = -1;

    private Solution bestSolution = new Solution(null, MAX_VALUE);

    private Data data;

    private short numberOfCities;
    private long testedFlights = 0;
    private short[] actualPath;
    private BitSet visitedCities;

    private volatile boolean stopped = false;

    @Override
    public Solution getBestSolution() {
        return bestSolution;
    }

    @Override
    public void init() {
        this.data = Salesman.data;
        numberOfCities = Salesman.cityNameMapper.getNumberOfCities();
        actualPath = new short[numberOfCities - 1];
        for (int i = 0; i < actualPath.length; i++) {
            actualPath[i] = NO_CITY;
        }
        visitedCities = new BitSet(numberOfCities);
    }

    @Override
    public void start() {
        short actualDay = 0;
        short actualCity = data.startCity;
        int actualPrice = 0;

        doNextFlight(actualDay, actualCity, actualPrice);

        logger.info("BruteForce Ended - testedFlights= " + testedFlights);
    }

    private void doNextFlight(short actualDay, short actualCity, int actualPrice) {
        if (stopped) {
            return;
        }

        Day day = data.days[actualDay];
        City city = day.cities[actualCity];
        for (int actualFlight = 0; actualFlight < city.flights.length; actualFlight++) {
            testedFlights++;
            Flight flight = city.flights[actualFlight];
            short nextCity = flight.destination;

            //do not flight, where you already been  ||  flight to start, before last day
            if (visitedCities.get(nextCity) || (nextCity == data.startCity && !(actualDay >= numberOfCities - 1)) )  {
                continue;
            }

            short nextDay = (short) (actualDay + 1);
            int nextPrice = actualPrice + flight.price;


            if (actualDay >= numberOfCities - 1) {
                if (nextCity == data.startCity) {
                    if (nextPrice < bestSolution.price) {
                        short[] pathCopy = Arrays.copyOf(actualPath, actualPath.length);
                        bestSolution = new Solution(pathCopy, nextPrice);
                        logger.info("New Best solution found. Price = " + nextPrice + ", path=" + Arrays.toString(pathCopy) + ", testedFlights=" + testedFlights);
                    }
//                    logger.info("Solution found. Price = " + nextPrice + ", path=" + Arrays.toString(actualPath) + ", testedFlights="+testedFlights);
                }
                continue;
            }


            actualPath[actualDay] = nextCity;
            visitedCities.set(nextCity, true);

//            logger.info("Fly from " + actualCity + " to " + nextCity);
            doNextFlight(nextDay, nextCity, nextPrice);
            actualPath[actualDay] = NO_CITY;
            visitedCities.set(nextCity, false);
        }
    }

    @Override
    public void stop() {
        stopped = true;
    }
}
