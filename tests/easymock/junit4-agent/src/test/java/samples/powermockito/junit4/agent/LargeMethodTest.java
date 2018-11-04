package samples.powermockito.junit4.agent;

import org.junit.Ignore;
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
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.method;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.suppress;

@PrepareForTest(MethodExceedingJvmLimit.class)
public class LargeMethodTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Ignore
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

    @Ignore
    @Test
    public void largeMethodShouldBeAbleToBeSuppressed() {
        suppress(method(MethodExceedingJvmLimit.class, "init"));
        assertNull("Suppressed method should return: null", MethodExceedingJvmLimit.init());
    }

    @Ignore
    @Test
    public void largeMethodShouldBeAbleToBeMocked() {
        mockStatic(MethodExceedingJvmLimit.class);
        expect(MethodExceedingJvmLimit.init()).andReturn("ok");
        replayAll();
        assertEquals("Mocked method should return: ok", "ok", MethodExceedingJvmLimit.init());
    }

    @Ignore
    @Test(expected = IllegalStateException.class)
    public void largeMethodShouldBeAbleToBeMockedAndThrowException() {
        mockStatic(MethodExceedingJvmLimit.class);
        expect(MethodExceedingJvmLimit.init()).andThrow(new IllegalStateException());
        replayAll();
        MethodExceedingJvmLimit.init();
    }
}
