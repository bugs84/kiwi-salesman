package cz.vondr.kiwi;

import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static cz.vondr.kiwi.Salesman.cityNameMapper;
import static java.lang.Integer.parseInt;
import static java.lang.Short.parseShort;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class InputParser {
    private final static Logger logger = LoggerFactory.getLogger(InputParser.class);

    private BufferedInputStream input;
    private Data data;

    public InputParser(InputStream input, Data data) {
        this.input = new BufferedInputStream(input);
        this.data = data;
    }

    public void readInputAndFillData() throws Exception {
        String line = null;
        readFirstLineWithStartTown();
        logger.info("First town was read.");
        //TODO DO NOT READ WHOLE LINE

        //TODO
        // 2.4s - input.readLine()
        // 1.2s - char[] chars = new char[8024];   input.read(chars)!=-1

//        char[] chars = new char[8024];
//        while (input.read(chars)!=-1) {


        // 9.5s - jen parsovani bez vytvareni objektu
        //19.2s - i s vytvarenim objektu
        while(readAndParseLine()) {
        }

        // 8.4s - jen parsovani bez vytvareni objektu
        // 17.3s- i s vytvarenim objektu
//        while ((line = input.readLine()) != null) {
//            parseLine(line);
//        }
        logger.info("Last town was read.");

        //cteni 2s  parsovani 7s   vytvareni objektu 7s  celkem 18s
    }


    private void readFirstLineWithStartTown() throws Exception {
        input.read(cityBytes);

        String startTown = new String(cityBytes, ISO_8859_1);
        data.startCity = cityNameMapper.nameToIndex(startTown);
        input.read();//eof
    }

    private byte[] cityBytes = new byte[3];

    private boolean readAndParseLine() throws IOException {


        if (input.read(cityBytes) == -1) {
            return false; //end of stream
        }


        short from = cityNameMapper.nameToIndex(new String(cityBytes, ISO_8859_1));
        input.read();//space
        input.read(cityBytes);
        short to = cityNameMapper.nameToIndex(new String(cityBytes, ISO_8859_1));
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

    private void parseLine(String line) {
        //TODO do not use regexp
        String[] split = line.split(" ");

        //parse line
        short from = cityNameMapper.nameToIndex(split[0]);
        short to = cityNameMapper.nameToIndex(split[1]);
        short dayIndex = parseShort(split[2]);
        int price = parseInt(split[3]);
        addFlightToData(from, to, dayIndex, price);


    }

    private void addFlightToData(short from, short to, short dayIndex, int price) {
        //remove nonsence flight
        if (from == data.startCity && dayIndex != 0) { //lety z pocatecniho mesta. jiny nez prvni (0) den
//            logger.info("Nonsence Start - "+ ++nonsenceStart);

            return;
        }

        //TODO tohle nemusi zabrat spravne, pokud jeste neni znamy spravny pocet mest :(
        if(to == data.startCity && dayIndex!=cityNameMapper.getNumberOfCities()-1) {
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
