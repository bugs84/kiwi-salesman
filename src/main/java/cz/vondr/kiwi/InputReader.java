package cz.vondr.kiwi;

import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static cz.vondr.kiwi.Salesman.cityNameMapper;
import static java.lang.Integer.parseInt;
import static java.lang.Short.parseShort;

public class InputReader {
    private final static Logger logger = LoggerFactory.getLogger(InputReader.class);

    private BufferedReader input;
    private Data data;

    public InputReader(InputStream input, Data data) {
        this.input = new BufferedReader(new InputStreamReader(input));
        this.data = data;
    }

    public void readInputAndFillData() throws Exception {
        String line = null;
        readFirstLineWithStartTown();
        while ((line = input.readLine()) != null) {
            parseLine(line);
        }

    }

    private void readFirstLineWithStartTown() throws Exception {
        String startTown = input.readLine();
        data.startTown = cityNameMapper.nameToIndex(startTown);
    }

    private void parseLine(String line) {
        String[] split = line.split(" ");
        short from = cityNameMapper.nameToIndex(split[0]);
        short to = cityNameMapper.nameToIndex(split[1]);
        short day = parseShort(split[2]);
        int price = parseInt(split[3]);

        if (data.days.size() <= day) {
//            if (data.days.size() != day) {//just check
//                throw new IllegalArgumentException("Unexpected input data. Day:" + day + " but size: " + data.days.size() + "");
//            }
            data.days.add(new City());
        }
        City city = data.days.get(day);


        logger.info(line);
    }
}
