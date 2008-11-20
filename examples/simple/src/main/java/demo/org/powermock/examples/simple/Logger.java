package demo.org.powermock.examples.simple;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
	private PrintWriter writer;

    public Logger() {
        System.out.println("Initializing logger");
        try {
			writer = new PrintWriter(new FileWriter("logger.log"));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
    }

    public void log(String message) {
    	writer.println(message);
    }
}
