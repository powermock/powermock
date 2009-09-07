package samples.junit3.annotationbased;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import junit.framework.TestCase;

import org.powermock.api.easymock.powermocklistener.AnnotationEnabler;
import org.powermock.core.classloader.annotations.PowerMockListener;

import samples.Service;
import samples.annotationbased.AnnotationDemo;

/**
 * Verifies that PowerMock test listeners works correctly with setup methods
 * when this is supported by unit 3.
 */
@PowerMockListener(AnnotationEnabler.class)
public class AnnotationDemoWithSetupMethodTest extends TestCase {

	@org.powermock.api.easymock.annotation.Mock
	private Service serviceMock;

	private AnnotationDemo tested;

	@Override
	protected void setUp() throws Exception {
		tested = new AnnotationDemo(serviceMock);
	}

	public void ignored_testInjectionWorked() throws Exception {
		final String expected = "mock";
		expect(serviceMock.getServiceMessage()).andReturn(expected);

		replayAll();

		assertEquals(expected, tested.getServiceMessage());

		verifyAll();
	}

	public void testDummy() throws Exception {
	}
}
