package samples.junit4.singleton;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.singleton.SimpleStaticService;
import samples.singleton.StaticService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SimpleStaticService.class)
public class SimpleStaticServiceTest {

    @Test
    public void testMockStatic() throws Exception {
        mockStatic(SimpleStaticService.class);
        final String expected = "Hello altered World";

        expect(SimpleStaticService.say("hello")).andReturn("Hello altered World");
        replay(SimpleStaticService.class);

        final String actual = SimpleStaticService.say("hello");

        verify(SimpleStaticService.class);
        assertEquals("Expected and actual did not match", expected, actual);

        // Singleton still be mocked by now.
        try {
            SimpleStaticService.say("world");
            fail("Should throw AssertionError!");
        } catch (final AssertionError e) {
            assertEquals("\n  Unexpected method call say(\"world\"):", e.getMessage());
        }
    }
}
