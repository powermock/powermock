package demo.org.powermock.examples.tutorial.staticmocking.impl.withoutpowermock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import demo.org.powermock.examples.tutorial.common.annotation.Inject;
import demo.org.powermock.examples.tutorial.staticmocking.IServiceRegistrator;
import demo.org.powermock.examples.tutorial.staticmocking.impl.IdGenerator;
import demo.org.powermock.examples.tutorial.staticmocking.osgi.BundleContext;
import demo.org.powermock.examples.tutorial.staticmocking.osgi.ServiceRegistration;

/**
 * This refactored implementation demonstrate how one could test the
 * <code>ServiceRegistrator</code> class without using PowerMock.
 * 
 */
public class ServiceRegistratorWithoutPowerMock implements IServiceRegistrator {

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
	public ServiceRegistratorWithoutPowerMock() {
		serviceRegistrations = new ConcurrentHashMap<Long, ServiceRegistration>();
	}

	/**
	 * {@inheritDoc}
	 */
	public long registerService(String name, Object serviceImplementation) {
		ServiceRegistration registerService = bundleContext.registerService(name, serviceImplementation, null);
		final long id = generateId();
		serviceRegistrations.put(id, registerService);
		return id;
	}

	/**
	 * @return A new id
	 */
	protected long generateId() {
		return IdGenerator.generateNewId();
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
