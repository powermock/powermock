package samples.powermockito.junit.jupiter.agent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit.jupiter.PowerMockExtension;
import samples.singleton.SimpleStaticService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class to demonstrate static mocking with PowerMockito.
 */
@PrepareForTest(SimpleStaticService.class)
@ExtendWith(PowerMockExtension.class)
class MockStaticTest {

    @Test
    void testMockStatic() {
        String expected = "Hello altered World";
        mockStatic(SimpleStaticService.class);
        when(SimpleStaticService.say("hello")).thenReturn(expected);

        String actual = SimpleStaticService.say("hello");

        assertEquals(expected, actual);
    }
}
