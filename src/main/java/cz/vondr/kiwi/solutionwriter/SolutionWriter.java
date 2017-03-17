package cz.vondr.kiwi.solutionwriter;

import cz.vondr.kiwi.Solution;
import cz.vondr.kiwi.data.Data;
import cz.vondr.kiwi.data.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
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

    public void writeSolutionToSystemOutput() throws Exception {
        write(System.out);
    }

    public void writeSolutionToLog() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        write(stringBuilder);
        writeSolutionStringToLog(stringBuilder.toString());
    }

    public void writeSolutionToSystemOutputAndLog() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        write(new WriteInTwoAppendables(System.out, stringBuilder));
        writeSolutionStringToLog(stringBuilder.toString());
    }

    private void writeSolutionStringToLog(String solutionString) {
        logger.info("Solution:\n" + solutionString);
    }



    // sample output
//    53
//    NAP BRQ 0 10
//    BRQ FCO 1 40
//    FCO NAP 2 3
    public void write(Appendable appendable) throws Exception {
        if (solution.price == Integer.MAX_VALUE) {
            appendable.append("NO SOLUTION FOUND :(");
            return;
        }

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
                .append(cityNameMapper.indexToName(fromIndex).toString())
                .append(' ')
                .append(cityNameMapper.indexToName(toIndex).toString())
                .append(' ')
                .append(Short.toString(dayIndex))
                .append(' ')
                .append(Integer.toString(getFlightPrice(fromIndex, toIndex, dayIndex)))
                .append('\n');
    }

    private int getFlightPrice(short fromIndex, short toIndex, short dayIndex) {
        List<Flight> flights = Arrays.stream(data.days[dayIndex].cities[fromIndex].flights).filter(flight -> flight.destination == toIndex).collect(Collectors.toList());
        if (flights.size() != 1) {
            throw new IllegalStateException("Cannot output. Expected 1 flight, but was found " + flights.size() + ". From=" + fromIndex + ", to=" + toIndex + ", day=" + dayIndex + "");
        }
        return flights.get(0).price;
    }

}
