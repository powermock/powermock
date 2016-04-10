package samples.powermockito.junit4.bugs.github510;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(InterfaceWithStatic.class)
public class ClassUsesInterfaceTest {

    public ClassUsesInterface classUsesInterface;

    @Before
    public void setUp() throws Exception {
        classUsesInterface = new ClassUsesInterface();

        mockStatic(InterfaceWithStatic.class);
    }



    @Test
    public void testSaySomething() throws Exception {
        final String value = "Hi Man";
        when(InterfaceWithStatic.sayHello()).thenReturn(value);

        assertThat(classUsesInterface.saySomething()).isEqualTo(value);
    }
}