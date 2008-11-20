package demo.org.powermock.examples.simple;

import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.expectNew;
import static org.powermock.PowerMock.replayAll;
import static org.powermock.PowerMock.verifyAll;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class})
public class LoggerTest {
	
	@Test(expected=IllegalStateException.class)
	public void testException() throws Exception {
		expectNew(FileWriter.class, "logger.log").andThrow(new IOException());
		
		replayAll();
		new Logger();
	}
	
	@Test
	public void testLogger() throws Exception {
		PrintWriter printWriter = createMock(PrintWriter.class);
		printWriter.println("qwe");
		expectNew(PrintWriter.class, new Class[] { Writer.class }, new Object[] { EasyMock.anyObject() }).andReturn(printWriter);
		replayAll();
		Logger logger = new Logger();
		logger.log("qwe");
		verifyAll();
	}

}
