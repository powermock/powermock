package demo.org.powermock.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jUser {
	private static final Logger log = LoggerFactory.getLogger(Slf4jUser.class);

	public final String getMessage() {
		log.debug("getMessage!");
		return "log4j user";
	}
}
