package org.powermock.modules.test.junit4.rule.xstream;

import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import samples.staticinitializer.StaticInitializerExample;

import java.util.HashSet;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@SuppressStaticInitializationFor("samples.staticinitializer.StaticInitializerExample")
public class StaticInitializerExampleTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

	@Test
	public void testSupressStaticInitializerAndSetFinalField() throws Exception {
		assertNull("Should be null because the static initializer should be suppressed", StaticInitializerExample.getMySet());
		final HashSet<String> hashSet = new HashSet<String>();
		Whitebox.setInternalState(StaticInitializerExample.class, "mySet", hashSet);
		assertSame(hashSet, Whitebox.getInternalState(StaticInitializerExample.class, "mySet"));
	}
}
