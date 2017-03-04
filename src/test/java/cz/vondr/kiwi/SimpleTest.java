package cz.vondr.kiwi;

import org.junit.Test;

import java.io.InputStream;

public class SimpleTest {

    @Test
    public void testRun1() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("SampleInput1.txt");

        Salesman salesman = new Salesman(inputStream);
        salesman.start();
    }
}