package samples.powermockito.junit4.rule.objenesis.bugs.github512;

import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.singleton.StaticService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.method;
import static org.powermock.api.mockito.PowerMockito.suppress;

public class Github512Test {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    @PrepareForTest(StaticService.class)
    public void shouldSuppressMethodWithPrepareForTestOnMethod() {
        suppress(method(StaticService.class, "calculate"));
        assertThat(StaticService.calculate(1, 5)).isEqualTo(0);
    }
}
