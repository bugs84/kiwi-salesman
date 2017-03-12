package cz.vondr.kiwi.algorithm.time_based;

import java.util.BitSet;

public class Progress extends cz.vondr.kiwi.algorithm.pq.Progress {

    public final int[] prices;

    public Progress(short[] path, int[] prices, BitSet visitedCities, int price) {
        super(path, visitedCities, price);
        this.prices = prices;
    }
}
