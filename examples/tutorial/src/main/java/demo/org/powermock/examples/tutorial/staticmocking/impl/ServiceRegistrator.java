package demo.org.powermock.examples.tutorial.staticmocking.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import demo.org.powermock.examples.tutorial.common.annotation.Inject;
import demo.org.powermock.examples.tutorial.staticmocking.IServiceRegistrator;
import demo.org.powermock.examples.tutorial.staticmocking.osgi.BundleContext;
import demo.org.powermock.examples.tutorial.staticmocking.osgi.ServiceRegistration;

/**
 * An "OSGi"-ish implementation of the {@link IServiceRegistrator} interface.
 * The test for this class demonstrates static mocking as well as getting and
 * setting internal state.
 * 
 */
public class ServiceRegistrator implements IServiceRegistrator {

	@Inject
	private BundleContext bundleContext;

	/**
	 * Holds all services registrations that has been registered by this service
	 * registrator.
	 */
	private final Map<Long, ServiceRegistration> serviceRegistrations;

	/**
	 * Default constructor, initializes internal state.
	 */
	public ServiceRegistrator() {
		serviceRegistrations = new ConcurrentHashMap<Long, ServiceRegistration>();
	}

	/**
	 * {@inheritDoc}
	 */
	public long registerService(String name, Object serviceImplementation) {
		ServiceRegistration registerService = bundleContext.registerService(name, serviceImplementation, null);
		final long id = IdGenerator.generateNewId();
		serviceRegistrations.put(id, registerService);
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public void unregisterService(long id) {
		final ServiceRegistration registration = serviceRegistrations.remove(id);
		if (registration == null) {
			throw new IllegalStateException("Registration with id " + id + " has already been removed or has never been registered");
		}
		registration.unregister();
	}

}
