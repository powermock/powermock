package demo.org.powermock.examples.tutorial.staticmocking.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.mockStatic;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;
import static org.powermock.Whitebox.getInternalState;
import static org.powermock.Whitebox.setInternalState;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import demo.org.powermock.examples.tutorial.partialmocking.service.impl.ProviderServiceImpl;
import demo.org.powermock.examples.tutorial.staticmocking.osgi.BundleContext;
import demo.org.powermock.examples.tutorial.staticmocking.osgi.ServiceRegistration;

/**
 * The purpose of this test is to get 100% coverage of the
 * {@link ServiceRegistrator} class without any code changes to that class. To
 * achieve this you need learn how to mock static methods as well as how to set
 * and get internal state of an object.
 * <p>
 * While doing this tutorial please refer to the documentation on how to mock
 * static methods and bypass encapsulation at the PowerMock web site.
 */
// TODO Specify the PowerMock runner
// TODO Specify which classes that must be prepared for test
public class ServiceRegistratorTest_Tutorial {

	private BundleContext bundleContextMock;
	private ServiceRegistration serviceRegistrationMock;
	private ServiceRegistrator tested;

	@Before
	public void setUp() {
		// TODO Create a mock object of the BundleContext and ServiceRegistration classes
		// TODO Create a new instance of SampleServiceImpl and pass in the created mock objects to the constructor
		// TODO Prepare the IdGenerator for static mocking
	}

	@After
	public void tearDown() {		
		// TODO Set all references to null
	}

	/**
	 * Test for the {@link ServiceRegistrator#registerService(String, Object)}
	 * method.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testRegisterService() throws Exception {
		// TODO Set the bundle context mock to the correct field in the tested instance
		// TODO Expect the call to bundleContextMock.registerService(..) and return a mock
		// TODO Expect the static method call to IdGenerator.generateNewId() and return a known id
		// TODO Replay all mock objects used and the class containing the static method
		// TODO Perform the actual test and assert that the result matches the expectations
		// TODO Verify all mock objects used and the class containing the static method
		// TODO Assert that the serviceRegistrations map in the test instance has been updated correctly
	}

	/**
	 * Test for the {@link ServiceRegistrator#unregisterService(long)} method.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testUnregisterService() throws Exception {
		// TODO Create a new HashMap of ServiceRegistration's and add a new ServiceRegistration to the map.
		// TODO Set the new HashMap to the serviceRegistrations field in the tested instance 
		// TODO Expect the call to serviceRegistrationMock.unregister()
		// TODO Replay all mock objects used
		// TODO Perform the actual test and assert that the result matches the expectations
		// TODO Verify all mock objects used
		// TODO Assert that the serviceRegistrations map in the test instance has been updated correctly
	}

	/**
	 * Test for the {@link ServiceRegistrator#unregisterService(long)} method
	 * when the ID doesn't exist.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testUnregisterService_idDoesntExist() throws Exception {
		// TODO Create a new HashMap of ServiceRegistration's and set it to the serviceRegistrations field in the tested instance 
		// TODO Expect the call to serviceRegistrationMock.unregister() and throw an IllegalStateException
		// TODO Replay all mock objects used
		// TODO Perform the actual test and assert that the result matches the expectations
		// TODO Verify all mock objects used
		// TODO Assert that the serviceRegistrations map in the test instance has not been updated
	}
}
