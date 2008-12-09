package org.powermock.examples;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JclUser {
	private static final Log log = LogFactory.getLog(JclUser.class);

	public final String getMessage() {
		log.debug("getMessage!");
		return "log4j user";
	}
}