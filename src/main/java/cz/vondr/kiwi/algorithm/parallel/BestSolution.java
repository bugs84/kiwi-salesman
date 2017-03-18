package cz.vondr.kiwi.algorithm.parallel;

import cz.vondr.kiwi.Solution;

import static java.lang.Integer.MAX_VALUE;

public class BestSolution {

    private Solution bestSolution = new Solution(null, MAX_VALUE);

    public Solution getBestSolution() {
        return bestSolution;
    }

    /** synchronized and do not allow to insert worse solution, then curretn beset solution */
    public synchronized void setBestSolution(Solution newBestSolution) {
        if (newBestSolution.price < this.bestSolution.price) {
            this.bestSolution = newBestSolution;
        }
    }
}
