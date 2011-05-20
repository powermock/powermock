package samples.testng;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;
import samples.finalmocking.FinalDemo;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;
import static org.testng.Assert.assertEquals;

@PrepareForTest(FinalDemo.class)
public class FinalTest extends PowerMockTestCase {

    @Test
    public void test() throws Exception {
        final FinalDemo finalDemo = createMock(FinalDemo.class);

        expect(finalDemo.say("something")).andReturn("something else");

        replayAll();

        final String actual = finalDemo.say("something");

        verifyAll();

        assertEquals("something else", actual);
    }
}
