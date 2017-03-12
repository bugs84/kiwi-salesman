package cz.vondr.kiwi;

public class StopWatch {

    private static final int NOT_SET = -15698;
    private long start;

    private long result = NOT_SET;

    public StopWatch() {
        start();
    }

    public StopWatch start() {
        start = System.currentTimeMillis();
        return this;
    }

    public long splitTime() {
        return System.currentTimeMillis() - start;
    }

    public long stop() {
        result = splitTime();
        return result;
    }

    public long getResult() {
        return result;
    }

    @Override
    public String toString() {
        if (result == NOT_SET) {
            stop();
        }
//        String.format("%d min, %d sec, $d millis",
//                TimeUnit.MILLISECONDS.toMinutes(millis),
//                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
//                millis % 1000
//        );
        return String.format("%d,%03d s",
                result / 1000,
                result % 1000
        );
    }
}
