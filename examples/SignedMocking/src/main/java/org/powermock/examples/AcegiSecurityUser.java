package org.powermock.examples;

import org.acegisecurity.acl.basic.BasicAclEntry;
import org.acegisecurity.acl.basic.cache.NullAclEntryCache;

/**
 * A simple naive example of a class that uses a dependency in the Acegi
 * Security framework (which is signed). Using plain EasyMock you'd would not be
 * able to mock a class located in the Acegi Security jar file.
 */
public class AcegiSecurityUser {

	private NullAclEntryCache cache;

	public AcegiSecurityUser(NullAclEntryCache cache) {
		this.cache = cache;
	}

	public BasicAclEntry[] getDecisionVoters() {
		return cache.getEntriesFromCache(null);
	}
}
