package org.apache.mina.transport.socket.nio;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.concurrent.Executor;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * This test validates that the {@link PrepareForTest} annotation modifies the
 * class hierarchy and not only the specified classes.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { NioDatagramSession.class, NioProcessor.class })
public class PrepareForTestAnnotationTest {

	@Test
	public void assertThatPrepareForTestModifiesClassHierarchy() throws Exception {
		final String scheduleRemoveMethodName = "scheduleRemove";

		Executor executor = createMock(Executor.class);
		NioProcessor objectUnderTest = createPartialMock(NioProcessor.class, new String[] { scheduleRemoveMethodName }, executor);
		NioDatagramSession session = createMock(NioDatagramSession.class);

		expect(session.isConnected()).andReturn(false);
		expectPrivate(objectUnderTest, scheduleRemoveMethodName, session).once();

		replay(objectUnderTest, executor, session);

		assertFalse((Boolean) Whitebox.invokeMethod(objectUnderTest, "flushNow", session, 20L));

		verify(objectUnderTest, executor, session);
	}
}
