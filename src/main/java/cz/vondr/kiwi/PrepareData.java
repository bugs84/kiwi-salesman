package cz.vondr.kiwi;

import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

import static java.util.Collections.binarySearch;

public class PrepareData {
    private final static Logger logger = LoggerFactory.getLogger(PrepareData.class);

    private static final Comparator<Flight> FLIGHT_COMPARATOR = (o1, o2) -> o1.price - o2.price;

    private Data data;

    public PrepareData(Data data) {
        this.data = data;
    }

    void prepare() {
        data.numberOfCities = Salesman.cityNameMapper.getNumberOfCities();

        prepareCityPrices();

        //sort flights
        List<Day> days = data.daysInput;
        for (int dayI = 0; dayI < days.size(); dayI++) {
            Day day = days.get(dayI);
            List<City> cities = day.citiesInput;
            for (int cityI = 0; cityI < cities.size(); cityI++) {
                City city = cities.get(cityI);
                List<Flight> flights = city.flightsInput;
                flights.sort((f1, f2) -> {
                    //TODO nevim jestli to delam dobre.
                    int f1PricesIndex = binarySearch(data.cityPrices.get(f1.destination), f1.price);
                    int f2PricesIndex = binarySearch(data.cityPrices.get(f2.destination), f2.price);
                    return f1PricesIndex - f2PricesIndex;
                });

//                flights.sort(FLIGHT_COMPARATOR);
                city.flights = city.flightsInput.toArray(new Flight[city.flightsInput.size()]);
                city.flightsInput = null;
            }
            day.cities = day.citiesInput.toArray(new City[day.citiesInput.size()]);
            day.citiesInput = null;
        }
        data.days = data.daysInput.toArray(new Day[data.daysInput.size()]);
        data.daysInput = null;

        writeDataInfo();
    }

    private void writeDataInfo() {
        StringBuilder sb = new StringBuilder();
        data.cityPrices.forEach((k, v) -> {

                    Integer min = Collections.min(v);
                    Integer max = Collections.max(v);

                    OptionalDouble average = v.stream().mapToInt(i -> i).average();
                    sb.append("  ").append(String.format("%3s",k)).append(" = ").append(String.format("%3s",min)).append(", ").append(String.format("%4s",max)).append(", ").append(average.getAsDouble()).append('\n');
                }
        );
        logger.info("Average prices: \n" +
                sb.toString()
        );
//        data.cityPrices.forEach();
    }

    private void prepareCityPrices() {
        data.cityPrices.forEach((cityIndex, priceList) -> Collections.sort(priceList));
    }

}
