package samples.powermockito.junit4.rule.objenesis;


import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;
import org.powermock.mockpolicies.MockPolicyInterceptionSettings;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import samples.powermockito.junit4.rule.objenesis.Foo.Bar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PowerMockIgnore({"org.mockito.*","org.powermock.api.mockito.repackaged.*"})
@PrepareForTest(Foo.class)
@MockPolicy(PowerMockRuleTest.CustomPolicy.class)
public class PowerMockRuleTest {

    @Rule
    public final PowerMockRule rule = new PowerMockRule();

    @Test
    public void test1() {
        assertEquals(999, new Foo().m().getI());
    }

    @Test
    public void test2() {
        assertEquals(999, new Foo().m().getI());
    }

    public static class CustomPolicy implements PowerMockPolicy {

        @Override
        public void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings) {
        }

        @Override
        public void applyInterceptionPolicy(MockPolicyInterceptionSettings settings) {
            final Bar barMock = mock(Bar.class);
            when(barMock.getI()).thenReturn(999);
            settings.stubMethod(Whitebox.getMethod(Foo.class, "m"), barMock);
        }
    }

}