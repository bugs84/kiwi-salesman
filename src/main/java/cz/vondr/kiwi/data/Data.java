package cz.vondr.kiwi.data;

import java.util.ArrayList;
import java.util.List;

public class Data {
    public Day[] days;
    public List<Day> daysInput = new ArrayList<>();
    public List<Destination> destsArrivals = new ArrayList<>();
    public List<Destination> destsDepartures = new ArrayList<>();
    public List<FragmentsDestination> fragmentsDestsDays = new ArrayList<>();

    public short startCity = -1;

    public short numberOfCities;



}
