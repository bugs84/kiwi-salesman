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
        logger.info("Start.");
        new InputReader(input, data).readInputAndFillData();
        logger.info("All data was read.");
        BruteForceAlgorithm algorithm = new BruteForceAlgorithm(data);
        algorithm.start();
        Solution solution = algorithm.getBestSolution();
        new SolutionWriter(data, solution).writeSolutionToLog();

        logger.info("End.");
    }


}
