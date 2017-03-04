package cz.vondr.kiwi.algorithm.simple;

import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BruteForceAlgorithm {

    private final static Logger logger = LoggerFactory.getLogger(BruteForceAlgorithm.class);


    private Solution actualSolution;

    //           data.days.cities.flights
    private Data data;

    public BruteForceAlgorithm(Data data) {
        this.data = data;
    }

    public void start() {
        short actualDay = 0;
        short actualCity = data.startCity;

        doNextFlight(actualDay, actualCity);


    }

    private void doNextFlight(short actualDay, short actualCity) {
        Day day = data.days.get(actualDay);
        City city = day.cities.get(actualCity);
        for (int actualFlight = 0; actualFlight < city.flights.size(); actualFlight++) {
            Flight flight = city.flights.get(actualFlight);
            short nextCity = flight.destination;
            if (nextCity == data.startCity) {
                logger.info("I HAVE CIRCLE! actualDay = " + actualDay);
            }
            short nextDay = (short) (actualDay + 1);
            //TODO do next flight
            logger.info("Fly from " + actualCity + " to " + nextCity);
            doNextFlight(nextDay, nextCity);
        }
    }
}
