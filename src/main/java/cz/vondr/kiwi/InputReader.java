package cz.vondr.kiwi;

import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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
        data.startCity = cityNameMapper.nameToIndex(startTown);
    }

    private void parseLine(String line) {
        String[] split = line.split(" ");

        //parse line
        short from = cityNameMapper.nameToIndex(split[0]);
        short to = cityNameMapper.nameToIndex(split[1]);
        short dayIndex = parseShort(split[2]);
        int price = parseInt(split[3]);

        //add flight
        Day day = getDay(data.daysInput, dayIndex);
        City city = getCity(day.citiesInput, from);
        city.flightsInput.add(new Flight(to, price));
    }

    private Day getDay(List<Day> list, short index) {
        while (list.size() <= index) {
            list.add(new Day());
        }
        return list.get(index);
    }

    private City getCity(List<City> list, short index) {
        while (list.size() <= index) {
            list.add(new City());
        }
        return list.get(index);
    }
}
