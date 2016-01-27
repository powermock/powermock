package samples.powermockito.junit4.staticmocking;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.finalmocking.FinalDemo;
import samples.powermockito.junit4.finalmocking.FinalDemoTest;
import samples.singleton.StaticService;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;


@PrepareEverythingForTest
@RunWith(PowerMockRunner.class)
public class PrepareEverythingForTestTest {
    private static final String ARGUMENT = "hello";

    @Test
    /**copy from {@link MockStaticTest}*/
    public void testMockStaticNoExpectations() throws Exception {
        mockStatic(StaticService.class);
        assertNull(StaticService.say(ARGUMENT));

        // Verification is done in two steps using static methods.
        verifyStatic();
        StaticService.say(argThat(new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                final boolean equals = o.equals(ARGUMENT);
                return equals;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
        validateMockitoUsage();
    }

    @Test
    /**copy from {@link FinalDemoTest}*/
    public void assertMockFinalWithNoExpectationsWorks() throws Exception {

        FinalDemo tested = mock(FinalDemo.class);

        assertNull(tested.say(ARGUMENT));

        verify(tested).say(ARGUMENT);
    }
}

