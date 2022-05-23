package samples.powermockito.junit4.jacoco;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StaticMethods.class, InstanceMethods.class})
public class TargetTest {

    @Test
    public void shouldCalculateSomethingStatic() throws Exception {
        mockStatic(StaticMethods.class);

        doReturn(1).when(StaticMethods.class, "getSomeFactor");
        doReturn(1).when(StaticMethods.class, "max");

        when(StaticMethods.calculateSomething(10)).thenCallRealMethod();

        assertThat(StaticMethods.calculateSomething(10)).isEqualTo(10);
    }

    @Test
    public void shouldCalculateSomething() throws Exception {

        InstanceMethods instanceMethods = mock(InstanceMethods.class);

        doReturn(1).when(instanceMethods, "getSomeFactor");
        doReturn(1).when(instanceMethods, "max");

        when(instanceMethods.calculateSomething(10)).thenCallRealMethod();

        assertThat(instanceMethods.calculateSomething(10)).isEqualTo(10);
    }

}
