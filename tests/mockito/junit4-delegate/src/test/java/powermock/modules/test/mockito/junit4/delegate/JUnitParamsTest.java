package powermock.modules.test.mockito.junit4.delegate;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@PowerMockRunnerDelegate(JUnitParamsRunner.class)
@PrepareForTest(Math.class)
@RunWith(PowerMockRunner.class)
public class JUnitParamsTest {

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Math.class);
        when(Math.addExact(anyInt(), anyInt())).thenReturn(42);
    }

    @Test
    @Parameters({"11, 234", "-54, 43"})
    public void testSum(int a, int b) {
        Assert.assertEquals(42, Math.addExact(a, b));
    }
}