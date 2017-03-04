package cz.vondr.kiwi;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

public class SimpleTest {

    @Test
    public void testRun1() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("SampleInput1.txt");

        Salesman salesman = new Salesman(inputStream);
        salesman.start();
    }


    @Test
    public void realData5() throws Exception {
        try (InputStream inputStream = new FileInputStream("c:\\prac\\Java\\Projects\\kiwi-salesman\\RealData\\data_5.txt")) {
            Salesman salesman = new Salesman(inputStream);
            salesman.start();
        }
    }
}