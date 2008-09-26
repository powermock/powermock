package powermock.examples.staticmocking;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.PowerMock.mockStatic;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * An example on how to mock the call to a static method.
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(IdGenerator.class)
public class ServiceRegistratorTest {

	@Test
	public void testRegisterService() throws Exception {
		long expectedId = 42;

		// We create a new instance of test class under test as usually.
		ServiceRegistrator tested = new ServiceRegistrator();

		// This is the way to tell PowerMock to mock all static methods of a
		// given class
		mockStatic(IdGenerator.class);

		/*
		 * The static method call to IdGenerator.generateNewId() expectation.
		 * This is why we need PowerMock.
		 */
		expect(IdGenerator.generateNewId()).andReturn(expectedId);

		// Note how we replay the class, not the instance!
		replay(IdGenerator.class);

		long actualId = tested.registerService(new Object());

		// Note how we verify the class, not the instance!
		verify(IdGenerator.class);

		// Assert that the ID is correct
		assertEquals(expectedId, actualId);
	}
}
