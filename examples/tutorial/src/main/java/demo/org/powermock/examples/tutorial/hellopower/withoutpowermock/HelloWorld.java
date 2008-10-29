package demo.org.powermock.examples.tutorial.hellopower.withoutpowermock;

/**
 * Use our own code instead of the third party code.
 */
public class HelloWorld {
	private ConfigWrapper config;
	
	public HelloWorld(ConfigWrapper config) {
		this.config = config;
	}
	
	public String greet() {
		return config.getGreeting() + " " + config.getTarget();
	}
}
