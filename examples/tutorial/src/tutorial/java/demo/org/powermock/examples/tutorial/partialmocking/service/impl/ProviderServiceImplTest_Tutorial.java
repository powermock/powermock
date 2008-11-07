package demo.org.powermock.examples.tutorial.partialmocking.service.impl;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.expectPrivate;
import static org.powermock.PowerMock.createPartialMock;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;
import static org.powermock.Whitebox.invokeMethod;
import static org.powermock.Whitebox.setInternalState;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import demo.org.powermock.examples.tutorial.domainmocking.impl.SampleServiceImpl;
import demo.org.powermock.examples.tutorial.partialmocking.dao.ProviderDao;
import demo.org.powermock.examples.tutorial.partialmocking.dao.domain.impl.ServiceArtifact;
import demo.org.powermock.examples.tutorial.partialmocking.domain.ServiceProducer;

/**
 * The purpose of this test is to get 100% coverage of the
 * {@link ProviderServiceImpl} class without any code changes to that class. To
 * achieve this you need learn how to create partial mocks, modify internal
 * state, invoke and expect private methods.
 * <p>
 * While doing this tutorial please refer to the documentation on how to expect
 * private methods and bypass encapsulation at the PowerMock web site.
 */
// TODO Specify the PowerMock runner
// TODO Specify which classes that must be prepared for test
public class ProviderServiceImplTest_Tutorial {
	
	private ProviderServiceImpl tested;
	private ProviderDao providerDaoMock;


	@Before
	public void setUp() {
		// TODO Create a mock object of the ProviderDao class
		// TODO Create a new instance of ProviderServiceImpl
		// TODO Set the providerDao mock to the providerDao field in the tested instance
	}

	@After
	public void tearDown() {
		// TODO Set all references to null
	}

	@Test
	public void testGetAllServiceProviders() throws Exception {
		// TODO Create a partial mock of the ProviderServiceImpl mocking only the getAllServiceProducers method
		// TODO Create a new HashSet of ServiceProducer's and add a ServiceProducer to the set
		// TODO Expect the private method call to getAllServiceProducers and return the created HashSet
		// TODO Replay all mock objects used
		// TODO Perform the actual test and assert that the result matches the expectations  
		// TODO Verify all mock objects used
	}

	@Test
	public void testGetAllServiceProviders_noServiceProvidersFound() throws Exception {
		// TODO Create a partial mock of the ProviderServiceImpl mocking only the getAllServiceProducers method
		// TODO Expect the private method call to getAllServiceProducers and return null
		// TODO Replay all mock objects used
		// TODO Perform the actual test and assert that the result matches the expectations 
		// TODO Verify all mock objects used 
	}

	@Test
	public void testServiceProvider_found() throws Exception {
		// TODO Create a partial mock of the ProviderServiceImpl mocking only the getAllServiceProducers method
		// TODO Create a new HashSet of ServiceProducer's and add a ServiceProducer to the set
		// TODO Expect the private method call to getAllServiceProducers and return the created HashSet
		// TODO Replay all mock objects used
		// TODO Perform the actual test and assert that the result matches the expectations  
		// TODO Verify all mock objects used
	}

	@Test
	public void testServiceProvider_notFound() throws Exception {
		// TODO Create a partial mock of the ProviderServiceImpl mocking only the getAllServiceProducers method
		// TODO Expect the private method call to getAllServiceProducers and return null
		// TODO Replay all mock objects used
		// TODO Perform the actual test and assert that the result matches the expectations 
		// TODO Verify all mock objects used 
	}

	@Test
	public void getAllServiceProducers() throws Exception {
		// TODO Create a new ServiceArtifact and a new HashSet place the created ServiceArtifact in this set
		// TODO Expect the call to the providerDao.getAllServiceProducers(..) and return the HashSet
		// TODO Replay all mock objects used
		// TODO Perform the actual test by invoking the private "getAllServiceProducers" method. Assert that the result matches the expectations.
		// TODO Verify all mock objects used
	}

	@Test
	public void getAllServiceProducers_empty() throws Exception {
		// TODO Create a new HashSet of ServiceArtifacts
		// TODO Replay all mock objects used
		// TODO Perform the actual test by invoking the private "getAllServiceProducers" method. Assert that the result matches the expectations.
		// TODO Verify all mock objects used
	}
}
