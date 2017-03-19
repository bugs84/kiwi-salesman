package cz.vondr.kiwi.algorithm.pq;

import java.util.BitSet;

import static cz.vondr.kiwi.Salesman.data;

public class Progress {

    /**
     * startCity is not in path. Not in the begging, not in the end
     **/
    public final short[] path;
    public final BitSet visitedCities;

    public final int price;


    public short flightsProcessed = 0;
//    public int nextFlightPrice;

    public int priorityPenalty = 1;

    public Progress(short[] path, BitSet visitedCities, int price) {
        this.path = path;
        this.visitedCities = visitedCities;
        this.price = price;
    }

    public Progress(short[] path, BitSet visitedCities, int price, int priorityPenalty) {
        this.path = path;
        this.visitedCities = visitedCities;
        this.price = price;
        this.priorityPenalty = priorityPenalty;
    }

    public short getDayIndex() {
        return (short) path.length;
    }

    public short getActualCity() {
        if (path.length == 0) {
            return data.startCity;
        } else {
            return path[path.length - 1];
        }
    }
}
