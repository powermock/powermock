package demo.org.powermock.examples.tutorial.staticmocking.impl;


/**
 * The purpose of the IdGenerator is to generate ID's.
 */
public class IdGenerator {

	/**
	 * @return A new random ID.
	 */
	public static long generateNewId() {
		return System.currentTimeMillis();
	}
}
