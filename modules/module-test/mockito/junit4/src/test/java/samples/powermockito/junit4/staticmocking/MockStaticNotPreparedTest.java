package samples.powermockito.junit4.staticmocking;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.powermock.api.mockito.ClassNotPreparedException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.singleton.StaticService;

import static org.hamcrest.CoreMatchers.containsString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 *
 */
@RunWith(PowerMockRunner.class)
public class MockStaticNotPreparedTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testWhenNotPrepared() throws Exception {

        assertOverwrittenException();

    }

    @PrepareForTest(StaticService.class)
    @Test
    public void testWhenPreparedButMockStaticIsNotCalled() throws Exception {

        assertOverwrittenException();

    }


    @Test(expected = ClassNotPreparedException.class)
    public void testWhenNotPreparedAndMockStaticIsCalled() throws Exception {

        mockStatic(StaticService.class);

    }

    private void assertOverwrittenException() {
        expectedException.expect(MissingMethodInvocationException.class);
        expectedException.expectMessage(containsString("PrepareForTest"));

        final String expected = "Hello world";
        final String argument = "hello";

        when(StaticService.say(argument)).thenReturn(expected);
    }
}
