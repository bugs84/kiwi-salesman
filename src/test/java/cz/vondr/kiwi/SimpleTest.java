package cz.vondr.kiwi;

import org.junit.Test;

import java.io.InputStream;

public class SimpleTest {

    @Test
    public void testRun1() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("SampleInput1.txt");

        Salesman.input = is;
        Salesman salesman = new Salesman();
        salesman.start();
    }
}