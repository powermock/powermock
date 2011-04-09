package samples.junit4.swing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.swing.ReallySimpleSwingDemo;

import javax.swing.*;

import static org.powermock.api.easymock.PowerMock.*;

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
