package demo.org.powermock.examples.tutorial.partialmocking.dao;

import java.util.Set;

import demo.org.powermock.examples.tutorial.partialmocking.dao.domain.impl.ServiceArtifact;

public interface ProviderDao {

	/**
	 * @return A set of all available service producers.
	 */
	Set<ServiceArtifact> getAllServiceProducers();

}
