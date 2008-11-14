package demo.org.powermock.examples.simple;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

public class FileLogger {
	
	private static final String ERROR_MESSAGE = "Failed to write file";
	private String fileName;

    public void write(String msg) {
    	PrintWriter out;
    	try {
			out = new PrintWriter(new FileWriter(fileName));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, ERROR_MESSAGE);
			throw new IllegalStateException(ERROR_MESSAGE, e);
		}
		try {
			out.println(msg);
		} finally {
			out.close();
		}
    	
    }
}
