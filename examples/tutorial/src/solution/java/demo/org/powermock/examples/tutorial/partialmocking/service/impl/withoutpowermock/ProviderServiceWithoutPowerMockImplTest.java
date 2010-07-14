/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.org.powermock.examples.tutorial.partialmocking.service.impl.withoutpowermock;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import demo.org.powermock.examples.tutorial.partialmocking.dao.ProviderDao;
import demo.org.powermock.examples.tutorial.partialmocking.dao.domain.impl.ServiceArtifact;
import demo.org.powermock.examples.tutorial.partialmocking.domain.ServiceProducer;

/**
 * Unit test for the {@link ProviderServiceWithoutPowerMockImpl} class.
 */
public class ProviderServiceWithoutPowerMockImplTest {

	private ProviderServiceWithoutPowerMockImpl tested;
	private ProviderDao providerDaoMock;

	@Before
	public void setUp() {
		providerDaoMock = createMock(ProviderDao.class);
		tested = new ProviderServiceWithoutPowerMockImpl(providerDaoMock);
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

		createPartialMock(methodNameToMock);

		expect(tested.getAllServiceProviders()).andReturn(expectedServiceProducers);

		replayAll(tested);

		Set<ServiceProducer> actualServiceProviders = tested.getAllServiceProviders();

		verifyAll(tested);

		assertSame(expectedServiceProducers, actualServiceProviders);
	}

	@Test
	public void testGetAllServiceProviders_noServiceProvidersFound() throws Exception {
		final String methodNameToMock = "getAllServiceProducers";
		final Set<ServiceProducer> expectedServiceProducers = new HashSet<ServiceProducer>();

		createPartialMock(methodNameToMock);

		expect(tested.getAllServiceProviders()).andReturn(null);

		replayAll(tested);

		Set<ServiceProducer> actualServiceProviders = tested.getAllServiceProviders();

		verifyAll(tested);

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

		createPartialMock(methodNameToMock);

		expect(tested.getAllServiceProducers()).andReturn(serviceProducers);

		replayAll(tested);

		ServiceProducer actual = tested.getServiceProvider(expectedServiceProducerId);

		verifyAll(tested);

		assertSame(expected, actual);
	}

	@Test
	public void testServiceProvider_notFound() throws Exception {
		final String methodNameToMock = "getAllServiceProducers";
		final int expectedServiceProducerId = 1;

		createPartialMock(methodNameToMock);

		expect(tested.getAllServiceProducers()).andReturn(new HashSet<ServiceProducer>());

		replayAll(tested);

		assertNull(tested.getServiceProvider(expectedServiceProducerId));

		verifyAll(tested);

	}

	@Test
	public void getAllServiceProducers() throws Exception {
		final String expectedName = "mock name";
		final int expectedId = 1;

		final Set<ServiceArtifact> serviceArtifacts = new HashSet<ServiceArtifact>();
		serviceArtifacts.add(new ServiceArtifact(expectedId, expectedName));

		expect(providerDaoMock.getAllServiceProducers()).andReturn(serviceArtifacts);

		replayAll();

		Set<ServiceProducer> serviceProducers = tested.getAllServiceProducers();

		verifyAll();

		assertEquals(1, serviceProducers.size());
		assertTrue(serviceProducers.contains(new ServiceProducer(expectedId, expectedName)));
	}

	@Test
	public void getAllServiceProducers_empty() throws Exception {
		expect(providerDaoMock.getAllServiceProducers()).andReturn(new HashSet<ServiceArtifact>());

		replayAll();

		Set<ServiceProducer> actual = tested.getAllServiceProducers();

		verifyAll();

		assertTrue(actual.isEmpty());
	}

	protected void replayAll(Object... additionalMocks) {
		replay(providerDaoMock);
		if (additionalMocks != null) {
			replay(additionalMocks);
		}
	}

	protected void verifyAll(Object... additionalMocks) {
		verify(providerDaoMock);
		if (additionalMocks != null) {
			verify(additionalMocks);
		}
	}

	@SuppressWarnings("deprecation")
	private void createPartialMock(final String methodNameToMock) throws NoSuchMethodException {
		Method getAllServiceProducersMethod = ProviderServiceWithoutPowerMockImpl.class.getDeclaredMethod(methodNameToMock);
		tested = createMock(ProviderServiceWithoutPowerMockImpl.class, new Method[] { getAllServiceProducersMethod });
	}
}
