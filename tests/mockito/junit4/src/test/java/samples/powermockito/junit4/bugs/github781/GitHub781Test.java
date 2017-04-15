package samples.powermockito.junit4.bugs.github781;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EqualsStatic.class)
public class GitHub781Test {

    private SpyObject partialMock = null;
    private final boolean result = true;

    @Test
    public void testCallMockStaticEquals() {
        PowerMockito.mockStatic(EqualsStatic.class);
        PowerMockito.when(EqualsStatic.equals()).thenReturn(result);
        partialMock = spy(new SpyObject());

        assertEquals(result, partialMock.callEquals());
    }
}
