package cz.vondr.kiwi;

import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static cz.vondr.kiwi.Salesman.cityNameMapper;

public class InputParser {
    private final static Logger logger = LoggerFactory.getLogger(InputParser.class);

    private DataInputStream input;
    private Data data;
    private byte[] cityBytes = new byte[3];

    public InputParser(InputStream input, Data data) {
        this.input = new DataInputStream(new BufferedInputStream(input));
        this.data = data;
    }

    public void readInputAndFillData() throws Exception {
        readFirstLineWithStartTown();
        logger.info("First town was read.");

        try {
            while (readAndParseLine()) {
            }
        }catch (EOFException eofe){
            logger.info("EOF reached - ok - all data was read.");
        }

        logger.info("Last town was read.");
    }

    private void readFirstLineWithStartTown() throws Exception {
        input.readFully(cityBytes);

        CityName startTown = new CityName(cityBytes[0], cityBytes[1], cityBytes[2]);
        data.startCity = cityNameMapper.nameToIndex(startTown);
        input.read();//eol
    }

    private boolean readAndParseLine() throws IOException {
        input.readFully(cityBytes);
        short from = cityNameMapper.nameToIndex(new CityName(cityBytes[0], cityBytes[1], cityBytes[2]));

        input.read();//space
        input.readFully(cityBytes);
        short to = cityNameMapper.nameToIndex(new CityName(cityBytes[0], cityBytes[1], cityBytes[2]));
        input.read();//space
        int charInt;
        short dayIndex = 0;
        while (true) {
            charInt = input.read();
            charInt = charInt - 48;
            if (charInt < 0) {
                break;
            }
            dayIndex = (short) (dayIndex * 10 + charInt);
        }

        int price = 0;
        while (true) {
            charInt = input.read();
            charInt = charInt - 48;
            if (charInt < 0) {
                break;
            }
            price = price * 10 + charInt;
        }

        addFlightToData(from, to, dayIndex, price);

        return charInt != -49;
    }

    private void addFlightToData(short from, short to, short dayIndex, int price) {
        //remove nonsence flight
        if (from == data.startCity && dayIndex != 0) { //lety z pocatecniho mesta. jiny nez prvni (0) den
//            logger.info("Nonsence Start - "+ ++nonsenceStart);

            return;
        }

        //TODO tohle nemusi zabrat spravne, pokud jeste neni znamy spravny pocet mest :(
        if (to == data.startCity && dayIndex != cityNameMapper.getNumberOfCities() - 1) {
//            = lety do pocatecniho mesta. jiny, nez posledni den
//            logger.info("Nonsence End - "+ ++nonsenceEnd);
            return;
        }

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
