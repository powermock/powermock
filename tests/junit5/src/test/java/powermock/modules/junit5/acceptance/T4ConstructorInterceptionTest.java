package powermock.modules.junit5.acceptance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit5.PowerMockExtension;
import samples.expectnew.ExpectNewDemo;
import samples.newmocking.MyClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/** T4: "new MyClass()" inside a prepared class returns the mock set up with whenNew. */
@ExtendWith(PowerMockExtension.class)
@PrepareForTest(ExpectNewDemo.class)
class T4ConstructorInterceptionTest {

    @Test
    void newInstanceCallReturnsMock() throws Exception {
        MyClass myClassMock = mock(MyClass.class);
        whenNew(MyClass.class).withNoArguments().thenReturn(myClassMock);
        when(myClassMock.getMessage()).thenReturn("from mock");

        // real MyClass.getMessage() returns "Hello world"
        assertEquals("from mock", new ExpectNewDemo().getMessage());

        verifyNew(MyClass.class).withNoArguments();
    }

    @Test
    void constructorCanBeMadeToThrow() throws Exception {
        whenNew(MyClass.class).withNoArguments().thenThrow(new IllegalStateException("ctor intercepted"));

        IllegalStateException e = org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> new ExpectNewDemo().getMessage());
        assertEquals("ctor intercepted", e.getMessage());
    }
}
