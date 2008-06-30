package demo.org.powermock.examples.tutorial.partialmocking.service;

import java.util.Set;

import demo.org.powermock.examples.tutorial.partialmocking.domain.ServiceProducer;

/**
 * A simple interface that manages persons.
 */
public interface ProviderService {

	/**
	 * Get all service provider.
	 * 
	 * @return All service provider artifacts currently available or an empty
	 *         set if no service providers are available.
	 */
	Set<ServiceProducer> getAllServiceProviders();

	/**
	 * Get a service provider.
	 * 
	 * @param id
	 *            The id of the service provider to get.
	 * @return The service artifact that represents the service provider or
	 *         <code>null</code> if no Service Producer was found with that
	 *         id.
	 */
	ServiceProducer getServiceProvider(int id);
}
