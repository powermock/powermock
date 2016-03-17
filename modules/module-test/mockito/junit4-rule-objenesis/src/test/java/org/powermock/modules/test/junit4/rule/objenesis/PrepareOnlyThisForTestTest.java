package org.powermock.modules.test.junit4.rule.objenesis;

import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.finalmocking.FinalDemo;
import samples.singleton.StaticService;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * verify that single {@link PrepareOnlyThisForTest} annotation works for the test
 */
@PrepareOnlyThisForTest({StaticService.class, FinalDemo.class})
public class PrepareOnlyThisForTestTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

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
