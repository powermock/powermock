package org.powermock.modules.junit4.strict;

import static org.powermock.PowerMock.mockMethod;
import static org.powermock.PowerMock.mockMethodStrict;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;
import static org.powermock.PowerMock.expectPrivate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.strict.StrictDemo;

/**
 * This is a simple test case for the {@link StrictDemo} class that demonstrates
 * that strict method mocking works.
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(StrictDemo.class)
public class StrictDemoTest {

	@Test
	public void testCallB_notStrict() throws Exception {
		StrictDemo tested = mockMethod(StrictDemo.class, "A", "B");
		expectPrivate(tested, "B").times(1);
		expectPrivate(tested, "A").times(1);

		replay(tested);

		tested.callAThenB();

		verify(tested);
	}

	@Test(expected = AssertionError.class)
	public void testCallB_strict_failure() throws Exception {
		StrictDemo tested = mockMethodStrict(StrictDemo.class, "A", "B");
		expectPrivate(tested, "B").times(1);
		expectPrivate(tested, "A").times(1);

		replay(tested);

		tested.callAThenB();

		verify(tested);
	}

	@Test
	public void testCallB_strict_ok() throws Exception {
		StrictDemo tested = mockMethodStrict(StrictDemo.class, "A", "B");
		expectPrivate(tested, "A").times(1);
		expectPrivate(tested, "B").times(1);

		replay(tested);

		tested.callAThenB();

		verify(tested);
	}
}
