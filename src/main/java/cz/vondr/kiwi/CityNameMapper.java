package cz.vondr.kiwi;

import java.util.HashMap;
import java.util.Map;

public class CityNameMapper {
    //         <CityName, CityIndex>
    private Map<String, Short> nameMap = new HashMap<>();
    private Map<Short, String> indexMap = new HashMap<>();

    public short nameToIndex(String cityName) {
        Short index = nameMap.get(cityName);
        if (index == null) {
            index = (short) (nameMap.size() + 1);
            nameMap.put(cityName, index);
            indexMap.put(index, cityName);
        }
        return index;
    }

    public String indexToName(short index) {
        return indexMap.get(index);
    }
}
