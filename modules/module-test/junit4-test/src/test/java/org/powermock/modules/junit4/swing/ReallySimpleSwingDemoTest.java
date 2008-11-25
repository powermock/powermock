package org.powermock.modules.junit4.swing;

import static org.powermock.PowerMock.expectLastCall;
import static org.powermock.PowerMock.mockStatic;
import static org.powermock.PowerMock.replayAll;
import static org.powermock.PowerMock.verifyAll;

import javax.swing.JOptionPane;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.swing.ReallySimpleSwingDemo;

/**
 * Unit test that makes sure that PowerMock works with Swing components.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(JOptionPane.class)
public class ReallySimpleSwingDemoTest {

	@Test
	public void assertThatPowerMockWorksWithSwingComponents() throws Exception {
		final String message = "powermock";

		mockStatic(JOptionPane.class);

		JOptionPane.showMessageDialog(null, message);
		expectLastCall().once();

		replayAll();

		new ReallySimpleSwingDemo().displayMessage(message);

		verifyAll();
	}
}
