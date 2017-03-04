package cz.vondr.kiwi.algorithm.simple;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static java.lang.Integer.MAX_VALUE;

public class BruteForceAlgorithm {

    private final static Logger logger = LoggerFactory.getLogger(BruteForceAlgorithm.class);

    private Solution bestSolution = new Solution(null, MAX_VALUE);

    //           data.days.cities.flights
    private Data data;

    private short numberOfCities = Salesman.cityNameMapper.getNumberOfCities();

    public BruteForceAlgorithm(Data data) {
        this.data = data;
    }

    public Solution getBestSolution() {
        return bestSolution;
    }

    public void start() {
        short actualDay = 0;
        short actualCity = data.startCity;
        short[] actualPath = new short[0];
        int actualPrice = 0;

        doNextFlight(actualDay, actualCity, actualPath, actualPrice);
    }

    private void doNextFlight(short actualDay, short actualCity, short[] actualPath, int actualPrice) {
        Day day = data.days.get(actualDay);
        City city = day.cities.get(actualCity);
        for (int actualFlight = 0; actualFlight < city.flights.size(); actualFlight++) {
            Flight flight = city.flights.get(actualFlight);
            short nextCity = flight.destination;

            //TODO instead of contains - use BitMask - to improve performance
            if (contains(actualPath, nextCity)) { //do not flight, where you already been
                break;
            }

            short nextDay = (short) (actualDay + 1);
            int nextPrice = actualPrice + flight.price;

            short[] nextPath = new short[actualPath.length + 1];
            System.arraycopy(actualPath, 0, nextPath, 0, actualPath.length);
            nextPath[nextPath.length - 1] = nextCity;

            if (actualDay >= numberOfCities - 1) {
                if (nextCity == data.startCity) {
                    if (nextPrice < bestSolution.price) {
                        bestSolution = new Solution(actualPath, nextPrice);
                    }
                    logger.info("Solution found. Price = " + nextPrice + ", path=" + Arrays.toString(actualPath));
                }
                break;
            }
            logger.info("Fly from " + actualCity + " to " + nextCity);
            doNextFlight(nextDay, nextCity, nextPath, nextPrice);
        }
    }

    private boolean contains(short[] array, short value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return true;
            }
        }
        return false;
    }

}
