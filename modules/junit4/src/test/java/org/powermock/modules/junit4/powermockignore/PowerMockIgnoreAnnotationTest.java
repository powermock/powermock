package org.powermock.modules.junit4.powermockignore;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Verifies the that the {@link PowerMockIgnore} annotation has precedence over
 * the {@link PrepareEverythingForTest} annotation.
 */
@RunWith(PowerMockRunner.class)
@PrepareEverythingForTest
@PowerMockIgnore("javax.swing.JComponent")
public class PowerMockIgnoreAnnotationTest {

	@Test
	public void assertCorrectClassloaders() throws Exception {
		assertNull(JComponent.class.getClassLoader());
		assertTrue(JOptionPane.class.getClassLoader().toString().contains(MockClassLoader.class.getSimpleName()));
	}

	@PrepareForTest
	@Test
	public void assertThatChunkingWorksWithPowerMockIgnore() throws Exception {
		assertNull(JComponent.class.getClassLoader());
		assertNull(JOptionPane.class.getClassLoader());
	}

	@PrepareOnlyThisForTest(JOptionPane.class)
	@PowerMockIgnore("javax.swing.JComponent")
	@Test
	public void assertThatChunkingWorksWithPowerMockIgnoreWhenAnnotationIsSpecifiedAtMethodLevel() throws Exception {
		assertNull(JComponent.class.getClassLoader());
		assertTrue(JOptionPane.class.getClassLoader().toString().contains(MockClassLoader.class.getSimpleName()));
	}
}
