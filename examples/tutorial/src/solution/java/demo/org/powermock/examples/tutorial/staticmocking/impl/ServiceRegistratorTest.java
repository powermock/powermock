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

import demo.org.powermock.examples.tutorial.staticmocking.osgi.BundleContext;
import demo.org.powermock.examples.tutorial.staticmocking.osgi.ServiceRegistration;

/**
 * Unit test for the {@link ServiceRegistrator} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(IdGenerator.class)
public class ServiceRegistratorTest {
	private ServiceRegistrator tested;

	private BundleContext bundleContextMock;

	private ServiceRegistration serviceRegistrationMock;

	/**
	 * {@inheritDoc}
	 */
	@Before
	public void setUp() {
		tested = new ServiceRegistrator();
		bundleContextMock = createMock(BundleContext.class);
		serviceRegistrationMock = createMock(ServiceRegistration.class);
		mockStatic(IdGenerator.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@After
	public void tearDown() {
		tested = null;
		bundleContextMock = null;
		serviceRegistrationMock = null;
	}

	/**
	 * Replay all mocks.
	 */
	protected void replayAll() {
		replay(bundleContextMock, serviceRegistrationMock);
		replay(IdGenerator.class);
	}

	/**
	 * Verify all mocks.
	 */
	protected void verifyAll() {
		verify(bundleContextMock, serviceRegistrationMock);
		verify(IdGenerator.class);
	}

	/**
	 * Test for the {@link ServiceRegistrator#registerService(String, Object)}
	 * method.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testRegisterService() throws Exception {
		final String name = "a name";
		final Object object = new Object();
		final long expectedId = 42;

		setInternalState(tested, "bundleContext", bundleContextMock);

		expect(bundleContextMock.registerService(name, object, null)).andReturn(serviceRegistrationMock);
		expect(IdGenerator.generateNewId()).andReturn(expectedId);

		replayAll();

		final long actualId = tested.registerService(name, object);

		verifyAll();

		Map<Long, ServiceRegistration> map = (Map<Long, ServiceRegistration>) getInternalState(tested, "serviceRegistrations");

		assertEquals(1, map.size());
		assertTrue("The id " + actualId + " was not found in the mServiceRegistrations map.", map.containsKey(actualId));
		assertTrue("The service " + serviceRegistrationMock + " was not found in the mServiceRegistrations map.", map
				.containsValue(serviceRegistrationMock));
		assertNotNull(map.get(expectedId));
	}

	/**
	 * Test for the {@link ServiceRegistrator#unregisterService(long)} method.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testUnregisterService() throws Exception {
		Map<Long, ServiceRegistration> map = new HashMap<Long, ServiceRegistration>();
		final long id = 1L;
		map.put(id, serviceRegistrationMock);

		setInternalState(tested, "serviceRegistrations", map);

		serviceRegistrationMock.unregister();
		expectLastCall().times(1);

		replayAll();

		tested.unregisterService(id);

		verifyAll();

		assertTrue("Map should be empty", map.isEmpty());

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
		Map<Long, ServiceRegistration> map = new HashMap<Long, ServiceRegistration>();
		final long id = 1L;

		setInternalState(tested, "serviceRegistrations", map);

		replayAll();

		try {
			tested.unregisterService(id);
			fail("Should throw IllegalStateException");
		} catch (IllegalStateException e) {
			assertEquals("Registration with id " + id + " has already been removed or has never been registered", e.getMessage());
		}

		verifyAll();

		assertTrue("Map should be empty", map.isEmpty());

	}
}
