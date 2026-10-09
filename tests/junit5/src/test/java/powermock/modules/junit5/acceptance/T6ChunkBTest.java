package powermock.modules.junit5.acceptance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit5.PowerMockExtension;
import samples.singleton.StaticService;
import samples.staticandinstance.StaticAndInstanceDemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * T6 (chunk B): prepares ONLY StaticAndInstanceDemo. StaticService is prepared and mocked
 * only in {@link T6ChunkATest}; here it must behave for real, whatever ran before.
 */
@ExtendWith(PowerMockExtension.class)
@PrepareForTest(StaticAndInstanceDemo.class)
class T6ChunkBTest {

    @Test
    void ownPreparedClassIsMockable() {
        mockStatic(StaticAndInstanceDemo.class);
        when(StaticAndInstanceDemo.getStaticMessage()).thenReturn("mocked in B");

        assertEquals("mocked in B", StaticAndInstanceDemo.getStaticMessage());
    }

    @Test
    void classPreparedOnlyInChunkAIsReal() {
        assertEquals("Hello chunk", StaticService.say("chunk"));
    }
}
