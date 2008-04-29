package powermock.examples;

import java.util.Set;

import powermock.examples.dependencymanagement.DependencyManager;
import powermock.examples.domain.Person;
import powermock.examples.service.MyService;

/**
 * A simple service class that uses the {@link DependencyManager} to get the
 * {@link MyService} singleton instance. This is the class that we want to test.
 * What's interesting in this example is the static call to the
 * <code>DependencyManager</code>. Without byte-code manipulation (provided
 * in this example by PowerMock) it would not be possible to return a mock from
 * the call to
 * 
 * <pre>
 * DependencyManager.getInstance();
 * </pre>
 * 
 * The purpose of this example is to demonastrate how to mock that static
 * method.
 */
public class MyServiceUser {

	public int getNumberOfPersons() {
		MyService myService = DependencyManager.getInstance().getMyService();
		Set<Person> allPersons = myService.getAllPersons();
		return allPersons.size();
	}

}
