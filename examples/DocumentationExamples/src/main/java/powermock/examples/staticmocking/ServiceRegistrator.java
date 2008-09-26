package powermock.examples.staticmocking;

import java.util.HashMap;
import java.util.Map;

public class ServiceRegistrator {

	/**
	 * Holds all services registrations that has been registered by this service
	 * registrator.
	 */
	private final Map<Long, Object> serviceRegistrations = new HashMap<Long, Object>();

	public long registerService(Object service) {
		final long id = IdGenerator.generateNewId();
		serviceRegistrations.put(id, service);
		return id;
	}

}
