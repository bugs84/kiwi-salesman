package cz.vondr.kiwi;

import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;

import java.util.Comparator;
import java.util.List;

public class PrepareData {

    private static final Comparator<Flight> FLIGHT_COMPARATOR = (o1, o2) -> o1.price - o2.price;

    private Data data;

    public PrepareData(Data data) {
        this.data = data;
    }

    void prepare() {
        //sort flights
        List<Day> days = data.daysInput;
        for (int dayI = 0; dayI < days.size(); dayI++) {
            Day day = days.get(dayI);
            List<City> cities = day.citiesInput;
            for (int cityI = 0; cityI < cities.size(); cityI++) {
                City city = cities.get(cityI);
                List<Flight> flights = city.flightsInput;
                flights.sort(FLIGHT_COMPARATOR);
                city.flights = city.flightsInput.toArray(new Flight[city.flightsInput.size()]);
                city.flightsInput = null;
            }
            day.cities = day.citiesInput.toArray(new City[day.citiesInput.size()]);
            day.citiesInput = null;
        }
        data.days = data.daysInput.toArray(new Day[data.daysInput.size()]);
        data.daysInput = null;
    }

}
