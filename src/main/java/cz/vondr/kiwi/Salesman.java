package cz.vondr.kiwi;

import cz.vondr.kiwi.algorithm.Algorithm;
import cz.vondr.kiwi.algorithm.pq.PriorityQueueAlgorithm;
import cz.vondr.kiwi.algorithm.simple.SimpleBruteForceAlgorithm;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.solutionwriter.SolutionWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Salesman {
    private final static Logger logger = LoggerFactory.getLogger(Salesman.class);

    private InputStream input;
    public static Data data = new Data();
    public static CityNameMapper cityNameMapper = new CityNameMapper();

    public Salesman(InputStream input) {
        this.input = input;
    }

    public void start() throws Exception {
        StopWatch wholeRun = new StopWatch();

        Algorithm algorithm;
        if (false) {
            algorithm = new PriorityQueueAlgorithm();
        } else {
            algorithm = new SimpleBruteForceAlgorithm();
        }

        CountDownLatch waitForAlgorithm = new CountDownLatch(1);
        Thread solutionWriteThread = new Thread() {
            @Override
            public void run() {
                try {
                                          //TODO jak dlouho pocitat, nez dam vysledek?
                    waitForAlgorithm.await(29_000, MILLISECONDS);

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
        new PrepareData(data).prepare();

        logger.info("Input parsed. In " + dataReadWatch);

        StopWatch algorithmWatch = new StopWatch();
        algorithm.init();
        algorithm.start();
        waitForAlgorithm.countDown();
        logger.info("Algorithm ended. In " + algorithmWatch);


        solutionWriteThread.join();

        logger.info("End. Whole run took: " + wholeRun);
    }


}
