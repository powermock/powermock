package powermock.examples.dependencymanagement;

import powermock.examples.service.MyService;
import powermock.examples.service.impl.MyServiceImpl;

/**
 * This is a simple example of a factory class that provides dependencies that
 * are shared by many classes (dependency lookup pattern). This approach is
 * quite common when dependency injection is not used.
 */
public final class DependencyManager {

	private static final DependencyManager instance = new DependencyManager();

	private MyService myService;

	private DependencyManager() {
	}

	public static DependencyManager getInstance() {
		return instance;
	}

	public synchronized MyService getMyService() {
		if (myService == null) {
			myService = new MyServiceImpl();
		}
		return myService;
	}
}
