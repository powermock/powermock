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
import static org.powermock.PowerMock.replayAll;
import static org.powermock.PowerMock.verifyAll;
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

import demo.org.powermock.examples.tutorial.partialmocking.dao.ProviderDao;
import demo.org.powermock.examples.tutorial.partialmocking.dao.domain.impl.ServiceArtifact;
import demo.org.powermock.examples.tutorial.partialmocking.domain.ServiceProducer;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ProviderServiceImpl.class)
public class ProviderServiceImplTest {

	private ProviderServiceImpl tested;
	private ProviderDao providerDaoMock;

	@Before
	public void setUp() {
		tested = new ProviderServiceImpl();
		providerDaoMock = createMock(ProviderDao.class);

		setInternalState(tested, providerDaoMock);
	}

	@After
	public void tearDown() {
		tested = null;
		providerDaoMock = null;
	}

	@Test
	public void testGetAllServiceProviders() throws Exception {
		final String methodNameToMock = "getAllServiceProducers";
		final Set<ServiceProducer> expectedServiceProducers = new HashSet<ServiceProducer>();
		expectedServiceProducers.add(new ServiceProducer(1, "mock name"));

		tested = createPartialMock(ProviderServiceImpl.class, methodNameToMock);

		expectPrivate(tested, methodNameToMock).andReturn(expectedServiceProducers);

		replayAll();

		Set<ServiceProducer> actualServiceProviders = tested.getAllServiceProviders();

		verifyAll();

		assertSame(expectedServiceProducers, actualServiceProviders);
	}

	@Test
	public void testGetAllServiceProviders_noServiceProvidersFound() throws Exception {
		final String methodNameToMock = "getAllServiceProducers";
		final Set<ServiceProducer> expectedServiceProducers = new HashSet<ServiceProducer>();

		tested = createPartialMock(ProviderServiceImpl.class, methodNameToMock);

		expectPrivate(tested, methodNameToMock).andReturn(null);

		replayAll();

		Set<ServiceProducer> actualServiceProviders = tested.getAllServiceProviders();

		verifyAll();

		assertNotSame(expectedServiceProducers, actualServiceProviders);
		assertEquals(expectedServiceProducers, actualServiceProviders);
	}

	@Test
	public void testServiceProvider_found() throws Exception {
		final String methodNameToMock = "getAllServiceProducers";
		final int expectedServiceProducerId = 1;
		final ServiceProducer expected = new ServiceProducer(expectedServiceProducerId, "mock name");

		final Set<ServiceProducer> serviceProducers = new HashSet<ServiceProducer>();
		serviceProducers.add(expected);

		tested = createPartialMock(ProviderServiceImpl.class, methodNameToMock);

		expectPrivate(tested, methodNameToMock).andReturn(serviceProducers);

		replayAll();

		ServiceProducer actual = tested.getServiceProvider(expectedServiceProducerId);

		verifyAll();

		assertSame(expected, actual);
	}

	@Test
	public void testServiceProvider_notFound() throws Exception {
		final String methodNameToMock = "getAllServiceProducers";
		final int expectedServiceProducerId = 1;

		tested = createPartialMock(ProviderServiceImpl.class, methodNameToMock);

		expectPrivate(tested, methodNameToMock).andReturn(new HashSet<ServiceProducer>());

		replayAll();

		assertNull(tested.getServiceProvider(expectedServiceProducerId));

		verifyAll();

	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllServiceProducers() throws Exception {
		final String expectedName = "mock name";
		final int expectedId = 1;

		final Set<ServiceArtifact> serviceArtifacts = new HashSet<ServiceArtifact>();
		serviceArtifacts.add(new ServiceArtifact(expectedId, expectedName));

		expect(providerDaoMock.getAllServiceProducers()).andReturn(serviceArtifacts);

		replayAll();

		Set<ServiceProducer> serviceProducers = (Set<ServiceProducer>) invokeMethod(tested, "getAllServiceProducers");

		verifyAll();

		assertEquals(1, serviceProducers.size());
		assertTrue(serviceProducers.contains(new ServiceProducer(expectedId, expectedName)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAllServiceProducers_empty() throws Exception {
		expect(providerDaoMock.getAllServiceProducers()).andReturn(new HashSet<ServiceArtifact>());

		replayAll();

		Set<ServiceProducer> actual = (Set<ServiceProducer>) invokeMethod(tested, "getAllServiceProducers");

		verifyAll();

		assertTrue(actual.isEmpty());
	}
}
