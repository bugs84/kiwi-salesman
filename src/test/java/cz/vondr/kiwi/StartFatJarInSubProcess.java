package cz.vondr.kiwi;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class StartFatJarInSubProcess {

    private void testFromFile() throws Exception {
        Path path = Paths.get("c:\\prac\\Java\\Projects\\kiwi-salesman\\RealData\\data_300.txt");
        byte[] bytes = Files.readAllBytes(path);

        ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(
                "java",
                "-jar",
                "c:\\prac\\Java\\Projects\\kiwi-salesman\\build\\libs\\kiwi-salesman-1.0-SNAPSHOT-all.jar"
        ));

        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
//        processBuilder.inheritIO();

        Process process = processBuilder.start();
        OutputStream processIn = process.getOutputStream();
        processIn.write(bytes);
        processIn.flush();
        processIn.close();


        process.waitFor();

    }


    private void start() throws Exception {
        testFromFile();
    }

    public static void main(String[] args) throws Exception {
        StartFatJarInSubProcess app = new StartFatJarInSubProcess();
        app.start();
    }
}
