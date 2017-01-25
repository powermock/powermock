package samples.powermockito.testng.staticmocking;

import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;
import samples.singleton.StaticService;

import static org.powermock.api.mockito.PowerMockito.when;


@PrepareForTest(StaticService.class)
public class MockStaticPreparedWithoutMockStaticTest extends PowerMockTestCase {


    //FIXME? Cannot override reporter in Mockito2 @Test(expectedExceptions = MissingMethodInvocationException.class, expectedExceptionsMessageRegExp = "(?s).*PrepareForTest(?s).*", enabled = false)
    @Test(expectedExceptions = MissingMethodInvocationException.class, enabled = false)
    public void testWhenNotPrepared() throws Exception {

        when(StaticService.say("Hello")).thenReturn("Hello World!");

    }
}
