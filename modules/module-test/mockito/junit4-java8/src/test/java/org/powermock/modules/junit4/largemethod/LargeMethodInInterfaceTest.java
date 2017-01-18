package org.powermock.modules.junit4.largemethod;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.powermockito.junit4.largemethod.InterfaceMethodExceedingJvmLimit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest(InterfaceMethodExceedingJvmLimit.class)
public class LargeMethodInInterfaceTest {

    @Test
    public void largeMethodShouldBeOverridden() {
        try {
            InterfaceMethodExceedingJvmLimit.init();
            fail("Method should be overridden and exception should be thrown");
        } catch (Exception e) {
            assertSame(IllegalAccessException.class, e.getClass());
            assertTrue(e.getMessage().contains("Method was too large and after instrumentation exceeded JVM limit"));
        }
    }

    @Test
    public void largeMethodShouldBeAbleToBeSuppressed() {
        suppress(PowerMockito.method(InterfaceMethodExceedingJvmLimit.class, "init"));
        assertNull("Suppressed method should return: null", InterfaceMethodExceedingJvmLimit.init());
    }

    @Test
    public void largeMethodShouldBeAbleToBeMocked() {
        mockStatic(InterfaceMethodExceedingJvmLimit.class);
        when(InterfaceMethodExceedingJvmLimit.init()).thenReturn("ok");
        assertEquals("Mocked method should return: ok", "ok", InterfaceMethodExceedingJvmLimit.init());
        verifyStatic();
    }

    @Test(expected = IllegalStateException.class)
    public void largeMethodShouldBeAbleToBeMockedAndThrowException() {
        mockStatic(InterfaceMethodExceedingJvmLimit.class);
        when(InterfaceMethodExceedingJvmLimit.init()).thenThrow(new IllegalStateException());
        InterfaceMethodExceedingJvmLimit.init();
        verifyStatic();
    }
}
