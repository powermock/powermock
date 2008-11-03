package org.apache.mina.transport.socket.nio;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.createPartialMock;
import static org.powermock.PowerMock.expectPrivate;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import java.util.concurrent.Executor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
