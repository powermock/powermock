package demo.org.powermock.examples.tutorial.domainmocking;

/**
 * A simple service interface.
 */
public interface SampleService {

	/**
	 * Create a new person based on the following parameters and store it in the
	 * underlying persistence store. The service will notify the result of the
	 * operation to an event service.
	 * 
	 * @param firstName
	 *            The first name of the person to create.
	 * @param lastName
	 *            The last name of the person to create.
	 * @return <code>true</code> if the person was created successfully,
	 *         <code>false</code> otherwise.
	 */
	boolean createPerson(String firstName, String lastName);

}
