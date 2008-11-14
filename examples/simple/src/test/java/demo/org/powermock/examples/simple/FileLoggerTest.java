package demo.org.powermock.examples.simple;

import static org.powermock.PowerMock.expectLastCall;
import static org.powermock.PowerMock.expectNew;
import static org.powermock.PowerMock.mockStatic;
import static org.powermock.PowerMock.replayAll;
import static org.powermock.Whitebox.getInternalState;
import static org.powermock.Whitebox.setInternalState;

import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({FileLogger.class, JOptionPane.class})
public class FileLoggerTest {
	
	@Test(expected=IllegalStateException.class)
	public void testException() throws Exception {
		expectNew(FileWriter.class, "out.log").andThrow(new IOException());
		
		mockStatic(JOptionPane.class);
		JOptionPane.showMessageDialog(null, getInternalState(FileLogger.class, String.class));

		expectLastCall();

		replayAll();
		FileLogger fileLogger = new FileLogger();
		setInternalState(fileLogger, "out.log");
		fileLogger.write("message");
		
	}

}
