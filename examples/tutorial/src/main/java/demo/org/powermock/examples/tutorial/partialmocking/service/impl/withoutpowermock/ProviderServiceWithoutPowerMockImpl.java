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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import demo.org.powermock.examples.tutorial.common.annotation.Inject;
import demo.org.powermock.examples.tutorial.partialmocking.dao.ProviderDao;
import demo.org.powermock.examples.tutorial.partialmocking.dao.domain.impl.ServiceArtifact;
import demo.org.powermock.examples.tutorial.partialmocking.domain.ServiceProducer;
import demo.org.powermock.examples.tutorial.partialmocking.service.ProviderService;

/**
 * We've slightly refactored the <code>ProviderService</code> to make the
 * class easier to test without PowerMock. What we've done is to use constructor
 * injection for the <code>providerDao</code> instead of field injection.
 * We've also refactored the {@link #getAllServiceProducers()} method to be
 * protected instead of private.
 * <p>
 * Note no refactoring is actually needed to test this method without PowerMock,
 * everything PowerMock does in this case can be done manually using reflection.
 * It's however common to do this refactoring instead of using too much
 * reflection in the test code.
 * 
 */
public class ProviderServiceWithoutPowerMockImpl implements ProviderService {

	private ProviderDao providerDao;

	@Inject
	public ProviderServiceWithoutPowerMockImpl(ProviderDao providerDao) {
		if (providerDao == null) {
			throw new IllegalArgumentException("providerDao cannot be null");
		}

		this.providerDao = providerDao;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ServiceProducer> getAllServiceProviders() {
		final Set<ServiceProducer> serviceProducers = getAllServiceProducers();
		if (serviceProducers == null) {
			return Collections.emptySet();
		}
		return serviceProducers;
	}

	/**
	 * {@inheritDoc}
	 */
	public ServiceProducer getServiceProvider(int id) {
		Set<ServiceProducer> allServiceProducers = getAllServiceProducers();
		for (ServiceProducer serviceProducer : allServiceProducers) {
			if (serviceProducer.getId() == id) {
				return serviceProducer;
			}
		}
		return null;
	}

	protected Set<ServiceProducer> getAllServiceProducers() {
		Set<ServiceArtifact> serviceArtifacts = providerDao.getAllServiceProducers();
		Set<ServiceProducer> serviceProducers = new HashSet<ServiceProducer>();

		for (ServiceArtifact serviceArtifact : serviceArtifacts) {
			serviceProducers.add(new ServiceProducer(serviceArtifact.getId(), serviceArtifact.getName(), serviceArtifact.getDataProducers()));
		}
		return serviceProducers;
	}
}
