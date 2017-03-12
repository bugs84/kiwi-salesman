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

    //TODO
    //tohle by se dalo pouzit, abych si mohl do queue ulozit i rozpracovane mesta (ze se z nej letelo jen nekam a ne vsude)
    public short flightsProcessed = 0;
//    public int nextFlightPrice;

    public Progress(short[] path, BitSet visitedCities, int price) {
        this.path = path;
        this.visitedCities = visitedCities;
        this.price = price;
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
