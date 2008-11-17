package demo.org.powermock.examples.simple;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class Logger {
    private JTextArea textArea;

    public Logger() {
        System.out.println("Initializing logger");
        JFrame frame = new JFrame("logger");
        textArea = new JTextArea();
        frame.getContentPane().add(textArea);
        frame.setSize(640, 480);
        frame.setVisible(true);
        textArea.setEditable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void log(String message) {
        textArea.setText(textArea.getText() + "\n" + message);
    }
}
