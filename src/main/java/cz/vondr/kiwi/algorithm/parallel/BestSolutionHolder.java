package cz.vondr.kiwi.algorithm.parallel;

import cz.vondr.kiwi.Solution;

import static java.lang.Integer.MAX_VALUE;

public class BestSolutionHolder {

    private Solution bestSolution = new Solution(null, MAX_VALUE);

    public Solution get() {
        return bestSolution;
    }

    /** synchronized and do not allow to insert worse solution, then curretn beset solution */
    public synchronized void set(Solution newBestSolution) {
        if (newBestSolution.price < this.bestSolution.price) {
            this.bestSolution = newBestSolution;
        }
    }
}
