package cz.vondr.kiwi.algorithm.simple;

import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static cz.vondr.kiwi.Salesman.cityNameMapper;

public class SolutionWriter {
    private final static Logger logger = LoggerFactory.getLogger(SolutionWriter.class);

    private Data data;
    private Solution solution;

    public SolutionWriter(Data data, Solution solution) {
        this.data = data;
        this.solution = solution;
    }

    public void writeSolutionToLog() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        write(stringBuilder);
        logger.info("Solution:\n" + stringBuilder.toString());
    }

    // sample output
//    53
//    NAP BRQ 0 10
//    BRQ FCO 1 40
//    FCO NAP 2 3
    public void write(Appendable appendable) throws Exception {
        short[] path = solution.path;
        short day = 0;

        //first line price
        appendable.append(Integer.toString(solution.price)).append('\n');

        //first flight from startTown
        appendOneFlight(appendable, data.startCity, path[0], day++);

        //flights from path
        for (int i = 0; i < path.length - 1; i++) {
            appendOneFlight(appendable, path[i], path[i + 1], day++);
        }

        //last flight to startTown
        appendOneFlight(appendable, path[path.length - 1], data.startCity, day++);
    }

    private void appendOneFlight(Appendable appendable, short fromIndex, short toIndex, short dayIndex) throws Exception {
        appendable
                .append(cityNameMapper.indexToName(fromIndex))
                .append(' ')
                .append(cityNameMapper.indexToName(toIndex))
                .append(' ')
                .append(Short.toString(dayIndex))
                .append(' ')
                .append(Integer.toString(getFlightPrice(fromIndex, toIndex, dayIndex)))
                .append('\n');
    }

    private int getFlightPrice(short fromIndex, short toIndex, short dayIndex) {
        List<Flight> flights = data.days.get(dayIndex).cities.get(fromIndex).flights.stream().filter(flight -> flight.destination == toIndex).collect(Collectors.toList());
        if (flights.size() != 1) {
            throw new IllegalStateException("Cannot output. Expected 1 flight, but was found " + flights.size() + ". From=" + fromIndex + ", to=" + toIndex + ", day=" + dayIndex + "");
        }
        return flights.get(0).price;
    }

}
