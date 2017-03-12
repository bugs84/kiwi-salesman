package cz.vondr.kiwi;

import cz.vondr.kiwi.algorithm.Algorithm;
import cz.vondr.kiwi.algorithm.pq.PriorityQueueAlgorithm;
import cz.vondr.kiwi.algorithm.progressiveDepthPriorityAlgorithm.ProgressiveDepthPriorityQueueAlgorithm;
import cz.vondr.kiwi.algorithm.simple.SimpleBruteForceAlgorithm;
import cz.vondr.kiwi.algorithm.time_based.TimeBasedAlgorithm;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.solutionwriter.SolutionWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Salesman {
    private final static Logger logger = LoggerFactory.getLogger(Salesman.class);

    public static final int TOTAL_ALGORITHM_TIME = 29_000;
    public static final StopWatch actualTime = new StopWatch();

    private InputStream input;
    public static Data data = new Data();
    public static CityNameMapper cityNameMapper = new CityNameMapper();

    public Salesman(InputStream input) {
        this.input = input;
    }

    public void start() throws Exception {
        StopWatch wholeRun = actualTime.start();

        Algorithm algorithm;
        switch (1) {
            case 1:
                algorithm = new SimpleBruteForceAlgorithm();
                break;
            case 2:
                algorithm = new PriorityQueueAlgorithm();
                break;
            case 3:
                algorithm = new TimeBasedAlgorithm();
                break;
            case 4:
                algorithm = new ProgressiveDepthPriorityQueueAlgorithm();
                break;
            default:
                throw new IllegalStateException();
        }

        CountDownLatch waitForAlgorithm = new CountDownLatch(1);
        Thread solutionWriteThread = new Thread() {
            @Override
            public void run() {
                try {
                    //TODO jak dlouho pocitat, nez dam vysledek? Kdy se presne spusti casovac?
                    waitForAlgorithm.await(TOTAL_ALGORITHM_TIME, MILLISECONDS);

                    algorithm.stop();
                    Solution bestSolution = algorithm.getBestSolution();
                    StopWatch writeSolutionWatch = new StopWatch();
                    new SolutionWriter(data, bestSolution).writeSolutionToSystemOutputAndLog();
                    logger.info("Write solution ended. In " + writeSolutionWatch);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        solutionWriteThread.start();


        logger.info("Start.");
        StopWatch dataReadWatch = new StopWatch();
        new InputParser(input, data).readInputAndFillData();
        cityNameMapper.writeIndexMapToLog();
        new PrepareData(data).prepare();

        logger.info("Input parsed. In " + dataReadWatch);

        Thread algorithmThread = new Thread() {
            @Override
            public void run() {
                try {
                    StopWatch algorithmWatch = new StopWatch();
                    algorithm.init();
                    algorithm.start();
                    waitForAlgorithm.countDown();
                    logger.info("Algorithm ended. In " + algorithmWatch);
                } catch (Throwable t) {
                    logger.error("ALGORITHM FATAL ERROR: ", t);
                    // Jestli se tohle roseka, tak se modlim,
                    // ze to mezitim dalo nejaky dobry, reseni, ktery ten vypisovaci thread vypise
                    // :)
                }
            }
        };
        algorithmThread.start();


        solutionWriteThread.join();
        algorithmThread.join();

        logger.info("Total End. Whole run took: " + wholeRun);
    }


}
