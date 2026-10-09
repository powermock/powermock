package powermock.modules.junit5.acceptance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit5.PowerMockExtension;
import samples.singleton.StaticService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/** T1: static methods of a prepared class can be stubbed and verified. */
@ExtendWith(PowerMockExtension.class)
@PrepareForTest(StaticService.class)
class T1StaticMockingTest {

    @Test
    void stubbedStaticMethodReturnsMockedValueAndIsVerified() {
        mockStatic(StaticService.class);
        when(StaticService.say("world")).thenReturn("mocked");

        assertEquals("mocked", StaticService.say("world"));

        verifyStatic(StaticService.class, times(1));
        StaticService.say("world");
    }

    @Test
    void staticFinalMethodCanBeStubbed() {
        mockStatic(StaticService.class);
        when(StaticService.sayFinal("world")).thenReturn("mocked final");

        assertEquals("mocked final", StaticService.sayFinal("world"));
    }
}
