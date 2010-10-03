package samples.powermockito.junit4.whennew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.verifyNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.expectnew.ExpectNewDemo;
import samples.newmocking.MyClass;

@PrepareForTest(ExpectNewDemo.class)
@RunWith(PowerMockRunner.class)
public class VerifyNewWithoutWhenNewTest {

    @Test
    // TODO This should actually work in the future when issue 148 is resolved.
    public void verifyingNewWithoutExpectationWhenNoArgumentsThrowsISE() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();

        assertEquals("Hello world", tested.getMessage());

        try {
            verifyNew(MyClass.class).withNoArguments();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("No instantiation of class samples.newmocking.MyClass was recorded "
                    + "during the test. Note that only expected object creations "
                    + "(e.g. those using whenNew(..)) can be verified.", e.getMessage());
        }

    }

    @Test
    // TODO This should actually work in the future when issue 148 is resolved.
    public void verifyingNewWithoutExpectationButWithArgumentsThrowsISE() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        assertEquals("Hello world", tested.getMessage());

        try {
            verifyNew(MyClass.class, Mockito.atLeastOnce()).withNoArguments();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("No instantiation of class samples.newmocking.MyClass was recorded "
                    + "during the test. Note that only expected object creations "
                    + "(e.g. those using whenNew(..)) can be verified.", e.getMessage());
        }
    }
}
