package samples.powermockito.testng.staticmocking;

import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.powermock.api.mockito.ClassNotPreparedException;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;
import samples.singleton.StaticService;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


public class MockStaticNotPreparedTest extends PowerMockTestCase {


    @Test(expectedExceptions = ClassNotPreparedException.class)
    public void testMockStatic() throws Exception {

        mockStatic(StaticService.class);

    }


    @Test(expectedExceptions = MissingMethodInvocationException.class, expectedExceptionsMessageRegExp =
                                                                               "(?s).*PrepareForTest(?s).*")
    public void testWhenNotPrepared() throws Exception {

        when(StaticService.say("Hello")).thenReturn("Hello World!");

    }
}
