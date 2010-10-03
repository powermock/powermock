package samples.testng;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.powermock.api.easymock.annotation.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import samples.Service;
import samples.annotationbased.AnnotationDemo;

/**
 * Verifies that PowerMock test listeners works correctly with before methods in
 * TestNG.
 */
public class AnnotationDemoWithBeforeMethodTest {

	@Mock
	private Service serviceMock;

	private AnnotationDemo tested;

	@BeforeMethod
	public void setup() {
		tested = new AnnotationDemo(serviceMock);
	}

	@Test
	public void assertInjectionWorked() throws Exception {
		final String expected = "mock";
		expect(serviceMock.getServiceMessage()).andReturn(expected);

		replayAll();

		Assert.assertEquals(expected, tested.getServiceMessage());

		verifyAll();
	}
}
