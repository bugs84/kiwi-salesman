package cz.vondr.kiwi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CityNameMapper {

    private final static Logger logger = LoggerFactory.getLogger(CityNameMapper.class);

    //         <CityName, CityIndex>
    private Map<CityName, Short> nameMap = new HashMap<>();
    private Map<Short, CityName> indexMap = new HashMap<>();

    public short nameToIndex(CityName cityName) {
        Short index = nameMap.get(cityName);
        if (index == null) {
            index = (short) (nameMap.size());
            nameMap.put(cityName, index);
            indexMap.put(index, cityName);
        }
        return index;
    }

    public CityName indexToName(short index) {
        return indexMap.get(index);
    }

    public short getNumberOfCities() {
        return (short) indexMap.size();
    }

    public void writeIndexMapToLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("Indexes and City name mapping:").append('\n');
        indexMap.forEach((index, city) ->
                sb.append(String.format("% 3d", index)).append(" : ").append(city).append('\n')
        );
        logger.info(sb.toString());
    }
}
