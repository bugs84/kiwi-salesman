package cz.vondr.kiwi;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

public class SimpleTest {


//    @Rule
//    public Stopwatch stopwatch = new Stopwatch() {
//        @Override
//        protected void succeeded(long nanos, Description description) {
//            System.out.println(description.toString() + ". Succeeded - " + nanos);
//        }
//
//        @Override
//        protected void failed(long nanos, Throwable e, Description description) {
////            logInfo(description, "failed", nanos);
//        }
//
//        @Override
//        protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
////            logInfo(description, "skipped", nanos);
//        }
//
//        @Override
//        protected void finished(long nanos, Description description) {
////            logInfo(description, "finished", nanos);
//        }
//    };


    @Test
    public void testRun1() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("SampleInput1.txt");

        Salesman salesman = new Salesman(inputStream);
        salesman.start();
    }


    @Test
    public void realData5() throws Exception {
        testFromFile("c:\\prac\\Java\\Projects\\kiwi-salesman\\RealData\\data_5.txt");
    }


    @Test
    public void realData10() throws Exception {
        testFromFile("c:\\prac\\Java\\Projects\\kiwi-salesman\\RealData\\data_10.txt");
    }

    @Test
    public void realData15() throws Exception {
        testFromFile("c:\\prac\\Java\\Projects\\kiwi-salesman\\RealData\\data_15.txt");
    }



    private void testFromFile(String inputDataFilePath) throws Exception {
        try (InputStream inputStream = new FileInputStream(inputDataFilePath)) {
            Salesman salesman = new Salesman(inputStream);
            salesman.start();
        }
    }
}