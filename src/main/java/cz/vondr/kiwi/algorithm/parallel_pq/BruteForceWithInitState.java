package cz.vondr.kiwi.algorithm.parallel_pq;

import cz.vondr.kiwi.Salesman;
import cz.vondr.kiwi.Solution;
import cz.vondr.kiwi.algorithm.parallel.BestSolutionHolder;
import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.BitSet;

public class BruteForceWithInitState {

    private final static Logger logger = LoggerFactory.getLogger(BruteForceWithInitState.class);
    public static final int NO_CITY = -1;

    private BestSolutionHolder bestSolutionHolder;

    private Data data;

    private short numberOfCities;
    private long testedFlights = 0;
    private short[] actualPath;
    private BitSet visitedCities;
    short actualDay = 0;

    private short initDayIndex;
    private short initFirstFlight;
    private short initLastFlight;
    private int initPrice;

    private volatile boolean stopped = false;

    public BruteForceWithInitState(BestSolutionHolder bestSolutionHolder) {
        this.bestSolutionHolder = bestSolutionHolder;
    }

    /**
     * length of actualPath is expected new short[numberOfCities - 1]
     * and actualPath is not copied
     */
    public void init(short[] actualPath, short initDayIndex, short initFirstFlight, short initLastFlight, int initPrice) {
        this.initDayIndex = initDayIndex;
        this.initFirstFlight = initFirstFlight;
        this.tmpFirstFlight = initFirstFlight;
        this.initLastFlight = initLastFlight;
        this.initPrice = initPrice;

        this.data = Salesman.data;
        numberOfCities = Salesman.cityNameMapper.getNumberOfCities();
        this.actualPath = actualPath;// new short[numberOfCities - 1];

        this.actualDay = initDayIndex;

        //init visitedCities
        visitedCities = new BitSet(numberOfCities);
        for (int i = 0; i < initDayIndex; i++) {
            visitedCities.set(this.actualPath[i]);
        }
    }

    //    @Override
    public void init() {
        this.data = Salesman.data;
        numberOfCities = Salesman.cityNameMapper.getNumberOfCities();
        actualPath = new short[numberOfCities - 1];
        for (int i = 0; i < actualPath.length; i++) {
            actualPath[i] = NO_CITY;
        }
        visitedCities = new BitSet(numberOfCities);
    }

    //    @Override
    public void start() throws Exception {

        short actualCity = actualDay > 0 ? actualPath[actualDay - 1] : data.startCity;
        int actualPrice = initPrice;

        doNextFlight(actualDay, actualCity, actualPrice);

//        logger.info("BruteForce Ended - testedFlights= " + testedFlights);
    }

    short tmpFirstFlight = 0;

    //TODO actual day do promenne (a pri navratu ho jen odecitat, ale nekde na to nezapomenout!  :-/
    private void doNextFlight(short actualDay, short actualCity, int actualPrice) throws Exception {
        if (stopped) {
            return;
        }
        if (testedFlights > 500_000) {
            return;
        }

        Day day = data.days[actualDay];
        City city = day.cities[actualCity];
        for (int actualFlight = tmpFirstFlight; actualFlight < city.flights.length; actualFlight++) {
            if (initDayIndex >= actualDay && actualFlight > initLastFlight) {
                return;
            }

            testedFlights++;
            Flight flight = city.flights[actualFlight];
            short nextCity = flight.destination;

            //do not flight, where you already been  ||  flight to start, before last day
            if (visitedCities.get(nextCity) || (nextCity == data.startCity && !(actualDay >= numberOfCities - 1))) {
                continue;
            }

            short nextDay = (short) (actualDay + 1);
            int nextPrice = actualPrice + flight.price;


            if (actualDay >= numberOfCities - 1) {
                if (nextCity == data.startCity) {
                    Solution bestSolution = this.bestSolutionHolder.get();
                    if (nextPrice < bestSolution.price) {
                        short[] pathCopy = Arrays.copyOf(actualPath, actualPath.length);
                        bestSolution = new Solution(pathCopy, nextPrice);
                        this.bestSolutionHolder.set(bestSolution);
                        logger.info("New Best solution found. Price = " + nextPrice + ", path=" + Arrays.toString(pathCopy) + ", testedFlights=" + testedFlights);
//                        new SolutionWriter(data, bestSolution).writeSolutionToLog();
                    }
//                    logger.info("Solution found. Price = " + nextPrice + ", path=" + Arrays.toString(actualPath) + ", testedFlights="+testedFlights);
                }
                continue;
            }

            if (nextPrice + data.minPriceEver*(numberOfCities - 1 - nextDay )>= bestSolutionHolder.get().price) { // very simple throw too expensive paths TODO do it even better
//                if (numberOfCities - actualDay > 10) {
//                    logger.info("faaaaar " + (numberOfCities - actualCity));
//                }
                continue;
            }

            actualPath[actualDay] = nextCity;
            visitedCities.set(nextCity, true);

//            logger.info("Fly from " + actualCity + " to " + nextCity);
            if (tmpFirstFlight != 0) tmpFirstFlight = 0;
            doNextFlight(nextDay, nextCity, nextPrice);
            actualPath[actualDay] = NO_CITY;
            visitedCities.set(nextCity, false);
        }
    }

    //    @Override
    public void stop() {
        stopped = true;
    }
}
