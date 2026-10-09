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
 * T6 (chunk A): prepares ONLY StaticService. StaticAndInstanceDemo is prepared and mocked
 * only in {@link T6ChunkBTest}; here it must behave for real, whatever ran before.
 */
@ExtendWith(PowerMockExtension.class)
@PrepareForTest(StaticService.class)
class T6ChunkATest {

    @Test
    void ownPreparedClassIsMockable() {
        mockStatic(StaticService.class);
        when(StaticService.say("chunk")).thenReturn("mocked in A");

        assertEquals("mocked in A", StaticService.say("chunk"));
    }

    @Test
    void classPreparedOnlyInChunkBIsReal() {
        assertEquals("hello world!", StaticAndInstanceDemo.getStaticMessage());
        assertEquals("Private hello world!", new StaticAndInstanceDemo().getMessage());
    }
}
