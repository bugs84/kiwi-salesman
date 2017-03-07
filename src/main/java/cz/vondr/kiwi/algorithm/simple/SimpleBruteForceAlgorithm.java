package cz.vondr.kiwi.algorithm.simple;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.BitSet;

import static java.lang.Integer.MAX_VALUE;

public class SimpleBruteForceAlgorithm {

    private final static Logger logger = LoggerFactory.getLogger(SimpleBruteForceAlgorithm.class);
    private static final int NO_CITY = -1;

    private Solution bestSolution = new Solution(null, MAX_VALUE);

    private Data data;

    private short numberOfCities = Salesman.cityNameMapper.getNumberOfCities();
    private long testedFlights = 0;
    private short[] actualPath = new short[numberOfCities - 1];
    private BitSet visitedTowns = new BitSet(numberOfCities);

    {
        //init actualPath
        for (int i = 0; i < actualPath.length; i++) {
            actualPath[i] = NO_CITY;
        }
    }

    public SimpleBruteForceAlgorithm(Data data) {
        this.data = data;
    }

    public Solution getBestSolution() {
        return bestSolution;
    }

    public void start() {
        short actualDay = 0;
        short actualCity = data.startCity;
        int actualPrice = 0;

        doNextFlight(actualDay, actualCity, actualPrice);

        logger.info("BruteForce Ended - testedFlights= " + testedFlights);
    }

    private void doNextFlight(short actualDay, short actualCity, int actualPrice) {
        testedFlights++;
        Day day = data.days[actualDay];
        City city = day.cities[actualCity];
        for (int actualFlight = 0; actualFlight < city.flights.length; actualFlight++) {
            Flight flight = city.flights[actualFlight];
            short nextCity = flight.destination;

            //do not flight, where you already been  ||  flight to start, before last day
            if (visitedTowns.get(nextCity) || (nextCity == data.startCity && !(actualDay >= numberOfCities - 1)) )  {
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
            visitedTowns.set(nextCity, true);

//            logger.info("Fly from " + actualCity + " to " + nextCity);
            doNextFlight(nextDay, nextCity, nextPrice);
            actualPath[actualDay] = NO_CITY;
            visitedTowns.set(nextCity, false);
        }
    }

}
