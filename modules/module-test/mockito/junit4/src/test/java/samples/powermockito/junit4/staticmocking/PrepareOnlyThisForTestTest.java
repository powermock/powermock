package samples.powermockito.junit4.staticmocking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.finalmocking.FinalDemo;
import samples.powermockito.junit4.finalmocking.FinalDemoTest;
import samples.singleton.StaticService;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@PrepareOnlyThisForTest({StaticService.class, FinalDemo.class})
@RunWith(PowerMockRunner.class)
public class PrepareOnlyThisForTestTest {

    @Test
    /**copy from {@link MockStaticTest}*/
    public void testMockStaticNoExpectations() throws Exception {
        mockStatic(StaticService.class);
        assertNull(StaticService.say("hello"));

        // Verification is done in two steps using static methods.
        verifyStatic();
        StaticService.say("hello");
        validateMockitoUsage();
    }

    @Test
    /**copy from {@link FinalDemoTest}*/
    public void assertMockFinalWithNoExpectationsWorks() throws Exception {
        final String argument = "hello";

        FinalDemo tested = mock(FinalDemo.class);

        assertNull(tested.say(argument));

        verify(tested).say(argument);
    }


}

