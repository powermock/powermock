package powermock.modules.junit5.acceptance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit5.PowerMockExtension;
import samples.finalmocking.FinalDemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/** T2: a final class (with final methods) can be mocked when prepared. */
@ExtendWith(PowerMockExtension.class)
@PrepareForTest(FinalDemo.class)
class T2FinalClassMockingTest {

    @Test
    void finalClassIsMockedAndStubbed() {
        FinalDemo finalDemo = mock(FinalDemo.class);
        when(finalDemo.say("world")).thenReturn("mocked");

        assertEquals("mocked", finalDemo.say("world"));
        verify(finalDemo).say("world");
    }
}
