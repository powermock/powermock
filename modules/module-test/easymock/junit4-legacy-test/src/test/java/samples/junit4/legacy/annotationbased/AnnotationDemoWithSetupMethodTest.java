package samples.junit4.legacy.annotationbased;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.modules.junit4.legacy.PowerMockRunner;

import samples.Service;
import samples.annotationbased.AnnotationDemo;

/**
 * Verifies that PowerMock test listeners works correctly with setup methods.
 */
@RunWith(PowerMockRunner.class)
public class AnnotationDemoWithSetupMethodTest {

	@Mock
	private Service serviceMock;

	private AnnotationDemo tested;

	@Before
	public void setup() {
		tested = new AnnotationDemo(serviceMock);
	}

	@Test
	public void assertInjectionWorked() throws Exception {
		final String expected = "mock";
		expect(serviceMock.getServiceMessage()).andReturn(expected);

		replayAll();

		assertEquals(expected, tested.getServiceMessage());

		verifyAll();
	}
}
