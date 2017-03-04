package cz.vondr.kiwi;

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
        new InputReader(input, data).readInputAndFillData();

        logger.info("ALL DATA READ.");
    }


}
