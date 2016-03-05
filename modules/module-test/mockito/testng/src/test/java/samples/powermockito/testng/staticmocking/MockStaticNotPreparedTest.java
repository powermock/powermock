package samples.powermockito.testng.staticmocking;

import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;
import samples.singleton.StaticService;

import static org.powermock.api.mockito.PowerMockito.when;


public class MockStaticNotPreparedTest extends PowerMockTestCase {


    @Test(expectedExceptions = MissingMethodInvocationException.class, expectedExceptionsMessageRegExp =
                                                                               "(?s).*PrepareForTest(?s).*")
    public void testMockito() throws Exception {

        when(StaticService.say("Hello")).thenReturn("Hello World!");

    }
}
