package samples.powermockito.junit4.enums;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.enummocking.EnumWithConstructor;
import samples.enummocking.SomeObjectInterfaceImpl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {EnumWithConstructor.class, SomeObjectInterfaceImpl.class}, fullyQualifiedNames = "samples.enummocking.EnumWithConstructor$1")
public class EnumWithConstructorTest {

    @Mock(name = "expectedSomeObjectInterfaceImpl")
    private SomeObjectInterfaceImpl someImplMock;

    @Test
    public void testCallMethodWithConstructor() throws Exception {

        whenNew(SomeObjectInterfaceImpl.class).withNoArguments().thenReturn(someImplMock);

        SomeObjectInterfaceImpl actual = (SomeObjectInterfaceImpl) EnumWithConstructor.SOME_ENUM_VALUE.create();

        assertThat(actual, is(sameInstance(someImplMock)));

    }
}
