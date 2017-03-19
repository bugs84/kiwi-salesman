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
    private static final int BB_SIZE = 64 * 1024;
    private byte[] bb = new byte[BB_SIZE];

    public InputParser(InputStream input, Data data) {
        this.input = new DataInputStream(new BufferedInputStream(input));
        this.data = data;
    }

    public void readInputAndFillData() throws Exception {
        readFirstLineWithStartTown();
        logger.info("First town was read.");

        readAndParseAllLines();

        logger.info("Last town was read.");
    }

    private int position = 0;

    private void readAndParseAllLines() throws IOException {
        while (true) {
            try {
                while (true) {
                    if (position == 0) {
                        bb = new byte[BB_SIZE];
                        input.readFully(bb, 0, BB_SIZE);
                    }
                    if (BB_SIZE - position > 17) {
                        short from = cityNameMapper.nameToIndex(new CityName(bb[position], bb[position + 1], bb[position + 2]));
                        short to = cityNameMapper.nameToIndex(new CityName(bb[position + 4], bb[position + 5], bb[position + 6]));
                        position += 7;

                        int charInt;
                        short dayIndex = 0;
                        while (true) {
                            position++;
                            charInt = bb[position];
                            charInt = charInt - 48;
                            if (charInt < 0) {
                                break;
                            }
                            dayIndex = (short) (dayIndex * 10 + charInt);
                        }

                        int price = 0;
                        while (true) {
                            position++;
                            charInt = bb[position];
                            charInt = charInt - 48;
                            if (charInt < 0) {
                                break;
                            }
                            price = price * 10 + charInt;
                        }
                        addFlightToData(from, to, dayIndex, price);
                        if (position == BB_SIZE - 1) {
                            position = 0;
                        } else {
                            position++;
                        }
                    } else {
                        if (BB_SIZE - position < 7) {
                            // nemame ani mesta
                            byte[] toRead = new byte[7 - (BB_SIZE - position)];
                            input.readFully(toRead);
                            byte[] cities = new byte[7];
                            int index = 0;
                            for (int i = position; i < BB_SIZE; i++) {
                                cities[index++] = bb[i];
                                position++;
                            }
                            for (byte b : toRead) {
                                cities[index++] = b;
                            }

                            short from = cityNameMapper.nameToIndex(new CityName(cities[0], cities[1], cities[2]));
                            short to = cityNameMapper.nameToIndex(new CityName(cities[4], cities[5], cities[6]));

                            input.read();
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
                            position = 0;
                        } else {
                            // mame mesta - musime cist, kdyz nam zbyde neco navic, tak budeme muset cist jeste jeden radek po bajtech
                            short from = cityNameMapper.nameToIndex(new CityName(bb[position++], bb[position++], bb[position++]));
                            position++;
                            short to = cityNameMapper.nameToIndex(new CityName(bb[position++], bb[position++], bb[position++]));

                            if (BB_SIZE - position == 0) {
                                //uz nemam v bb cisla
                                input.read();
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

                                position = 0;
                            } else {
                                // jeste mi v bb neco zbyva
                                position++;
                                int charInt;
                                short dayIndex = 0;
                                while (true) {
                                    if (position < BB_SIZE) {
                                        charInt = bb[position++];
                                    } else {
                                        charInt = input.read();
                                    }
                                    charInt = charInt - 48;
                                    if (charInt < 0) {
                                        break;
                                    }
                                    dayIndex = (short) (dayIndex * 10 + charInt);
                                }

                                int price = 0;
                                while (true) {
                                    if (position < BB_SIZE) {
                                        charInt = bb[position++];
                                    } else {
                                        charInt = input.read();
                                    }
                                    charInt = charInt - 48;
                                    if (charInt < 0) {
                                        break;
                                    }
                                    price = price * 10 + charInt;
                                }
                                addFlightToData(from, to, dayIndex, price);

                                if (position >= BB_SIZE) {
                                    position = 0;
                                } else {
                                    // tak nam jeste neco zbyva a musime to precist - novy radek
                                    // nemame ani mesta
                                    byte[] toRead = new byte[7 - (BB_SIZE - position)];
                                    input.readFully(toRead);
                                    byte[] cities = new byte[7];
                                    int index = 0;
                                    for (int i = position; i < BB_SIZE; i++) {
                                        cities[index++] = bb[i];
                                        position++;
                                    }
                                    for (byte b : toRead) {
                                        cities[index++] = b;
                                    }

                                    from = cityNameMapper.nameToIndex(new CityName(cities[0], cities[1], cities[2]));
                                    to = cityNameMapper.nameToIndex(new CityName(cities[4], cities[5], cities[6]));

                                    input.read();
                                    charInt = 0;
                                    dayIndex = 0;
                                    while (true) {
                                        charInt = input.read();
                                        charInt = charInt - 48;
                                        if (charInt < 0) {
                                            break;
                                        }
                                        dayIndex = (short) (dayIndex * 10 + charInt);
                                    }

                                    price = 0;
                                    while (true) {
                                        charInt = input.read();
                                        charInt = charInt - 48;
                                        if (charInt < 0) {
                                            break;
                                        }
                                        price = price * 10 + charInt;
                                    }
                                    addFlightToData(from, to, dayIndex, price);

                                    position = 0;
                                }
                            }
                        }
                    }

                }
            } catch (EOFException e) {
                // dat je min nez buffer size a precist 0..position
                while (bb[position] != 0) {
                    short from = cityNameMapper.nameToIndex(new CityName(bb[position++], bb[position++], bb[position++]));
                    position++;
                    short to = cityNameMapper.nameToIndex(new CityName(bb[position++], bb[position++], bb[position++]));
                    position++;
                    int charInt;
                    short dayIndex = 0;
                    while (true) {
                        charInt = bb[position++];
                        charInt = charInt - 48;
                        if (charInt < 0) {
                            break;
                        }
                        dayIndex = (short) (dayIndex * 10 + charInt);
                    }

                    int price = 0;
                    while (true) {
                        charInt = bb[position++];
                        charInt = charInt - 48;
                        if (charInt < 0) {
                            break;
                        }
                        price = price * 10 + charInt;
                    }
                    addFlightToData(from, to, dayIndex, price);
                }
                logger.info("EOF reached - ok - all data was read.");
            }
            break;
        }
    }

    private void readFirstLineWithStartTown() throws Exception {
        byte[] cb = new byte[3];
        input.readFully(cb);

        CityName startTown = new CityName(cb[0], cb[1], cb[2]);
        data.startCity = cityNameMapper.nameToIndex(startTown);
        input.read();//eol
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
