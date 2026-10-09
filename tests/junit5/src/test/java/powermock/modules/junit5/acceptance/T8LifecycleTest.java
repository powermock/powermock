package powermock.modules.junit5.acceptance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit5.PowerMockExtension;
import samples.singleton.StaticService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * T8: static mocking set up in @BeforeEach is seen by the test, and verified in @AfterEach.
 * If a lifecycle method ran outside PowerMock's class loader context it would mock/verify a
 * different StaticService than the one the test calls, and the assertions below fail.
 */
@ExtendWith(PowerMockExtension.class)
@PrepareForTest(StaticService.class)
class T8LifecycleTest {

    private static String greetingFromBeforeAll;

    @BeforeAll
    static void beforeAll() {
        greetingFromBeforeAll = StaticService.say("all");
    }

    @BeforeEach
    void setUp() {
        mockStatic(StaticService.class);
        when(StaticService.say("lifecycle")).thenReturn("from beforeEach");
    }

    @AfterEach
    void tearDown() {
        verifyStatic(StaticService.class, times(1));
        StaticService.say("lifecycle");
    }

    @Test
    void stubFromBeforeEachIsActiveInTest() {
        assertEquals("from beforeEach", StaticService.say("lifecycle"));
    }

    @Test
    void stubIsFreshForEachTest() {
        assertEquals("from beforeEach", StaticService.say("lifecycle"));
    }

    @Test
    void beforeAllRanAgainstTheRealClassBeforeAnyMocking() {
        // the test instance must see the static state written by @BeforeAll
        assertEquals("Hello all", greetingFromBeforeAll);
        assertEquals("from beforeEach", StaticService.say("lifecycle"));
    }
}
