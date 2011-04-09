package org.apache.mina.transport.socket.nio;

import org.apache.mina.core.session.AbstractIoSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.concurrent.Executor;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * This test validates that the {@link PrepareOnlyThisForTest} annotation
 * modifies the only the specified classes and not the full hierarchy.
 */
@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest( { NioDatagramSession.class, NioProcessor.class })
public class PrepareOnlyThisForTestAnnotationTest {

    @Test(expected = NullPointerException.class)
    public void assertThatPrepareOnlyThisForTestDoesntModifyClassHierarchy() throws Exception {
        NioDatagramSession session = createMock(NioDatagramSession.class);
        expect(session.isConnected()).andReturn(false);
    }

    @Test
    @PrepareOnlyThisForTest( { NioDatagramSession.class, NioProcessor.class, AbstractIoSession.class })
    public void assertThatPrepareOnlyThisForTestWorks() throws Exception {
        final String scheduleRemoveMethodName = "scheduleRemove";

        Executor executor = createMock(Executor.class);
        NioProcessor objectUnderTest = createPartialMock(NioProcessor.class, new String[] { scheduleRemoveMethodName }, executor);
        NioDatagramSession session = createMock(NioDatagramSession.class);

        expect(session.isConnected()).andReturn(false);
        expectPrivate(objectUnderTest, scheduleRemoveMethodName, session).once();

        replay(objectUnderTest, executor, session);

        assertFalse(Whitebox.<Boolean> invokeMethod(objectUnderTest, "flushNow", session, 20L));

        verify(objectUnderTest, executor, session);
    }

}
