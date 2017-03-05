package cz.vondr.kiwi;

import cz.vondr.kiwi.algorithm.simple.BruteForceAlgorithm;
import cz.vondr.kiwi.algorithm.simple.Solution;
import cz.vondr.kiwi.algorithm.simple.SolutionWriter;
import cz.vondr.kiwi.data.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

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

        logger.info("Start.");
        StopWatch dataReadWatch = new StopWatch();
        new InputReader(input, data).readInputAndFillData();
        new PrepareData(data).prepare();

        logger.info("Input parsed. In " + dataReadWatch);

        BruteForceAlgorithm algorithm = new BruteForceAlgorithm(data);
        StopWatch algorithmWatch = new StopWatch();
        algorithm.start();
        logger.info("Algorithm ended. In " + algorithmWatch);

        StopWatch writeSolutionWatch = new StopWatch();
        Solution solution = algorithm.getBestSolution();
        new SolutionWriter(data, solution).writeSolutionToLog();
        logger.info("Write solution ended. In " + writeSolutionWatch);


        logger.info("End. Whole run took: " + wholeRun);
    }


}
