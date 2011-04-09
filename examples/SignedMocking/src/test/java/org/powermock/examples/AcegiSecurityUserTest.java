package org.powermock.examples;

import org.acegisecurity.acl.basic.BasicAclEntry;
import org.acegisecurity.acl.basic.cache.NullAclEntryCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * Unit tests that demonstrates PowerMock's ability to mock classes in signed
 * jars.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(NullAclEntryCache.class)
public class AcegiSecurityUserTest {

	@Test
	public void testMockSigned() throws Exception {
		NullAclEntryCache cacheMock = createMock(NullAclEntryCache.class);
		AcegiSecurityUser tested = new AcegiSecurityUser(cacheMock);

		final BasicAclEntry[] basicAclEntries = new BasicAclEntry[0];
		expect(cacheMock.getEntriesFromCache(null)).andReturn(basicAclEntries);

		replay(cacheMock);

		assertSame(basicAclEntries, tested.getDecisionVoters());

		verify(cacheMock);
	}
}
