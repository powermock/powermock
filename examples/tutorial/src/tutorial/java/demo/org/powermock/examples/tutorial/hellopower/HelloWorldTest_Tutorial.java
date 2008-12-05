package demo.org.powermock.examples.tutorial.hellopower;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import demo.org.powermock.examples.tutorial.domainmocking.impl.SampleServiceImpl;

/**
 * The purpose of this test is to get 100% coverage of the {@link HelloWorld}
 * class without any code changes to that class. To achieve this you need learn
 * how to mock static methods.
 * <p>
 * While doing this tutorial please refer to the documentation on how to mock
 * static methods at the PowerMock web site.
 */
// TODO Specify the PowerMock runner
// TODO Specify which classes that must be prepared for test
public class HelloWorldTest_Tutorial {

	@Test
	public void testGreeting() {
		// TODO: mock the static methods of SimpleConfig
		// TODO: Replay the behavior
		// TODO: Perform the test of the greet method and assert that it returns the expected behavior
		// TODO: Verify the behavior
	}
}
