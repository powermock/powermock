package demo.org.powermock.examples.tutorial.hellopower.withoutpowermock;

import demo.org.powermock.examples.tutorial.hellopower.SimpleConfig;

/**
 * Encapsulate the use of the third party code in our own code that we can mock. 
 */
public class ConfigWrapper {
	public String getTarget() {
		return SimpleConfig.getTarget();
	}

	public String getGreeting() {
		return SimpleConfig.getGreeting();
	}
}
