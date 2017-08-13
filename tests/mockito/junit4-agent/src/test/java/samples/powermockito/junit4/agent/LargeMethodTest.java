package samples.powermockito.junit4.agent;

import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.largemethod.MethodExceedingJvmLimit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@PrepareForTest(MethodExceedingJvmLimit.class)
public class LargeMethodTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Test
    public void largeMethodShouldBeOverridden() {
        try {
            MethodExceedingJvmLimit.init();
            fail("Method should be overridden and exception should be thrown");
        } catch (Exception e) {
            assertSame(IllegalAccessException.class, e.getClass());
            assertTrue(e.getMessage().contains("Method was too large and after instrumentation exceeded JVM limit"));
        }
    }

    @Test
    public void largeMethodShouldBeAbleToBeSuppressed() {
        suppress(method(MethodExceedingJvmLimit.class, "init"));
        assertNull("Suppressed method should return: null", MethodExceedingJvmLimit.init());
    }

    @Test
    public void largeMethodShouldBeAbleToBeMocked() {
        mockStatic(MethodExceedingJvmLimit.class);
        when(MethodExceedingJvmLimit.init()).thenReturn("ok");
        assertEquals("Mocked method should return: ok", "ok", MethodExceedingJvmLimit.init());
        verifyStatic(MethodExceedingJvmLimit.class);
    }

    @Test(expected = IllegalStateException.class)
    public void largeMethodShouldBeAbleToBeMockedAndThrowException() {
        mockStatic(MethodExceedingJvmLimit.class);
        when(MethodExceedingJvmLimit.init()).thenThrow(new IllegalStateException());
        MethodExceedingJvmLimit.init();
        verifyStatic(MethodExceedingJvmLimit.class);
    }
}
