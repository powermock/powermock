package demo.org.powermock.examples.tutorial.domainmocking.domain;

/**
 * A simple exception that's thrown when an error occurs when executing a method
 * in the SampleService.
 */
public class SampleServiceException extends RuntimeException {
	private static final long serialVersionUID = -4496776468570486636L;

	public SampleServiceException(String description, Throwable cause) {
		super(description, cause);
	}

	public SampleServiceException(String description) {
		super(description);
	}

}
