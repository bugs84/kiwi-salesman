package cz.vondr.kiwi;

import cz.vondr.kiwi.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static cz.vondr.kiwi.Salesman.cityNameMapper;

public class InputParser {
    private final static Logger logger = LoggerFactory.getLogger(InputParser.class);

    private BufferedInputStream input;
    private Data data;
    private byte[] cityBytes = new byte[3];

    public InputParser(InputStream input, Data data) {
        this.input = new BufferedInputStream(input);
        this.data = data;
    }

    public void readInputAndFillData() throws Exception {
        readFirstLineWithStartTown();
        logger.info("First town was read.");

        while (readAndParseLine()) {
        }

        logger.info("Last town was read.");
    }

    private void readFirstLineWithStartTown() throws Exception {
        input.read(cityBytes);

        CityName startTown = new CityName(cityBytes);
        data.startCity = cityNameMapper.nameToIndex(startTown);
        input.read();//eol
    }

    private boolean readAndParseLine() throws IOException {
        if (input.read(cityBytes) == -1) {
            return false; //end of stream
        }
        short from = cityNameMapper.nameToIndex(new CityName(cityBytes));
        input.read();//space
        input.read(cityBytes);
        short to = cityNameMapper.nameToIndex(new CityName(cityBytes));
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

        // flight
        Flight flight = new Flight(to, price, from, dayIndex);

        //add flight
        Day day = getDay(data.daysInput, dayIndex);
        City city = getCity(day.citiesInput, from);
        city.flightsInput.add(flight);

        // arrival
        Destination destination = getDestination(data.destsArrivals, from);
        DayMiky dayMiky = getDayMiky(destination.days, dayIndex);
        dayMiky.flights.add(flight);

        // departure
        destination = getDestination(data.destsDepartures, to);
        dayMiky = getDayMiky(destination.days, dayIndex);
        dayMiky.flights.add(flight);
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

    private DayMiky getDayMiky(List<DayMiky> list, short index) {
        while (list.size() <= index) {
            list.add(new DayMiky());
        }
        return list.get(index);
    }

    private Destination getDestination(List<Destination> list, short index) {
        while (list.size() <= index) {
            list.add(new Destination());
        }
        return list.get(index);
    }

}
