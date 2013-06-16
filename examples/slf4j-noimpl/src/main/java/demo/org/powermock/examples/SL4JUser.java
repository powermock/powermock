package demo.org.powermock.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SL4JUser {
	final Logger logger = LoggerFactory.getLogger(SL4JUser.class);
	
	public boolean returnTrue() {
        logger.info("ikk");
		return true;
	}
}
