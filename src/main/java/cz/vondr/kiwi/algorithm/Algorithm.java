package cz.vondr.kiwi.algorithm;

import cz.vondr.kiwi.Solution;

public interface Algorithm {
    Solution getBestSolution();

    void init();

    void start() throws InterruptedException;

    void stop();
}
