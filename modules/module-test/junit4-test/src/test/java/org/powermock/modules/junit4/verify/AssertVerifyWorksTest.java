package org.powermock.modules.junit4.verify;

import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.PowerMock.mockStatic;
import static org.powermock.PowerMock.mockStaticPartial;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.singleton.StaticHelper;
import samples.singleton.StaticService;

/**
 * This test asserts that the
 * http://code.google.com/p/powermock/issues/detail?id=73 issue is resolved.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { StaticHelper.class, StaticService.class })
public class AssertVerifyWorksTest {

	@Test
	public void testMockStaticWorks() throws Exception {
		mockStaticPartial(StaticService.class, "sayHello");
		mockStatic(StaticHelper.class);

		StaticService.sayHello();
		expectLastCall().once();

		StaticHelper.sayHelloHelper();
		expectLastCall().once();

		replay(StaticService.class);
		replay(StaticHelper.class);

		StaticService.assertThatVerifyWorksForMultipleMocks();

		verify(StaticService.class);
		verify(StaticHelper.class);
	}
}
