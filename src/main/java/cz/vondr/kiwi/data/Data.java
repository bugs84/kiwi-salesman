package cz.vondr.kiwi.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {
    public Day[] days;
    public List<Day> daysInput = new ArrayList<>();

    public short startCity = -1;

    public short numberOfCities;

//             city,   prices
    public Map<Short, List<Integer>> cityPrices = new HashMap<>();



}
