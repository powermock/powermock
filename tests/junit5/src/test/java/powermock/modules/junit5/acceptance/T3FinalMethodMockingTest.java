package powermock.modules.junit5.acceptance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit5.PowerMockExtension;
import samples.privateandfinal.PrivateFinal;
import samples.simplemix.SimpleMix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/** T3: final methods of NON-final classes can be stubbed when the class is prepared. */
@ExtendWith(PowerMockExtension.class)
@PrepareForTest({SimpleMix.class, PrivateFinal.class})
class T3FinalMethodMockingTest {

    @Test
    void publicFinalMethodOfNonFinalClassIsStubbed() {
        // SimpleMix is not final, but calculate() is; unprepared, the real body runs (and throws NPE on a mock).
        SimpleMix simpleMix = mock(SimpleMix.class);
        when(simpleMix.calculate()).thenReturn(42);

        assertEquals(42, simpleMix.calculate());
        verify(simpleMix).calculate();
    }

    @Test
    void privateFinalMethodOfSpyIsStubbed() throws Exception {
        PrivateFinal tested = spy(new PrivateFinal());
        assertEquals("Hello test", tested.say("test"));

        when(tested, "sayIt", isA(String.class)).thenReturn("stubbed");

        assertEquals("stubbed", tested.say("test"));
    }
}
