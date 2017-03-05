package cz.vondr.kiwi;

import cz.vondr.kiwi.data.City;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Day;
import cz.vondr.kiwi.data.Flight;

import java.util.Comparator;
import java.util.List;

public class PrepareData {

    private static final Comparator<Flight> FLIGHT_COMPARATOR = new Comparator<Flight>() {
        @Override
        public int compare(Flight o1, Flight o2) {
            return o1.price - o2.price;
        }
    };


    private Data data;

    public PrepareData(Data data) {
        this.data = data;
    }

    void prepare() {
        //sort flights
        List<Day> days = data.days;
        for (int dayI = 0; dayI < days.size(); dayI++) {
            List<City> cities = days.get(dayI).cities;
            for (int cityI = 0; cityI < cities.size(); cityI++) {
                List<Flight> flights = cities.get(cityI).flights;
                flights.sort(FLIGHT_COMPARATOR);
            }
        }
    }

}
