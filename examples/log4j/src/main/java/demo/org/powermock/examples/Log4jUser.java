package demo.org.powermock.examples;


public class Log4jUser extends Log4jUserChild{

	public final String getMessage() {
		log.debug("getMessage!");
		return "log4j user";
	}

	public String mergeMessageWith(String otherMessage) {
		log.debug("mergeMessageWith!");
		return getMessage() + otherMessage;
	}
}
