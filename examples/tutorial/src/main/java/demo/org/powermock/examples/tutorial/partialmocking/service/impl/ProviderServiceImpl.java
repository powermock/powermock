package demo.org.powermock.examples.tutorial.partialmocking.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import demo.org.powermock.examples.tutorial.common.annotation.Inject;
import demo.org.powermock.examples.tutorial.partialmocking.dao.ProviderDao;
import demo.org.powermock.examples.tutorial.partialmocking.dao.domain.impl.ServiceArtifact;
import demo.org.powermock.examples.tutorial.partialmocking.domain.ServiceProducer;
import demo.org.powermock.examples.tutorial.partialmocking.service.ProviderService;

/**
 * A simple implementation of the providers service. This is the class that's
 * going to be tested using PowerMock. The main reason for the test is to
 * demonstrate how to use PowerMock to set internal state (i.e. setting the
 * <code>providerDao</code> field without setters), partial mocking and
 * expectations of private methods.
 */
public class ProviderServiceImpl implements ProviderService {

	@Inject
	private ProviderDao providerDao;

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

	private Set<ServiceProducer> getAllServiceProducers() {
		Set<ServiceArtifact> serviceArtifacts = providerDao.getAllServiceProducers();
		Set<ServiceProducer> serviceProducers = new HashSet<ServiceProducer>();

		for (ServiceArtifact serviceArtifact : serviceArtifacts) {
			serviceProducers.add(new ServiceProducer(serviceArtifact.getId(), serviceArtifact.getName(), serviceArtifact.getDataProducers()));
		}
		return serviceProducers;
	}
}
