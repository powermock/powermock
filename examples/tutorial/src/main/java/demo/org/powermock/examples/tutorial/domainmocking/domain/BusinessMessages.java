package demo.org.powermock.examples.tutorial.domainmocking.domain;

import java.util.Random;

/**
 * A simple domain object that's pretended to hold state of one or more
 * operation outcomes. We pretend that services can store messages or errors
 * (for example validation errors) in this message (even though these methods
 * are not implemented or even defined here).
 */
public class BusinessMessages {

	/**
	 * @return <code>true</code> if the an error has occurred when invoking an
	 *         operation, <code>false</code> otherwise.
	 */
	public boolean hasErrors() {
		return new Random().nextBoolean();
	}

}
