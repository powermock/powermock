package demo.org.powermock.examples.tutorial.hellopower;

public class HelloWorld {
	public String greet() {
		return SimpleConfig.getGreeting() + " " + SimpleConfig.getTarget();
	}
}
