package samples.powermockito.junit4.bugs.github510;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({InterfaceWithStatic.class, ConstructorObject.class})
public class Github510Test {

    public ClassUsesInterface classUsesInterface;

    @Before
    public void setUp() throws Exception {
        classUsesInterface = new ClassUsesInterface();
    }

    @Test
    public void testSaySomething() throws Exception {
        final String value = "Hi Man";

        mockStatic(InterfaceWithStatic.class);
        when(InterfaceWithStatic.sayHello()).thenReturn(value);

        assertThat(classUsesInterface.saySomething()).isEqualTo(value);
    }

    @Test
    public void testInterfaceStaticCallsConstructor() throws Exception {
        final String value = "Hi Man";

        ConstructorObject constructorObject = mock(ConstructorObject.class);
        when(constructorObject.sayHello()).thenReturn(value);


        whenNew(ConstructorObject.class).withNoArguments().thenReturn(constructorObject);

        assertThat(classUsesInterface.createAndSay()).isEqualTo(value);
    }
}