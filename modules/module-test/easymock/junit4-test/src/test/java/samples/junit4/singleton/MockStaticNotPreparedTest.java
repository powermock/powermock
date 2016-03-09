package samples.junit4.singleton;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.singleton.StaticService;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.replay;

/**
 *
 */
@RunWith(PowerMockRunner.class)
public class MockStaticNotPreparedTest {

    @Ignore("Easymock uses static methods and cglib, so call cannot be intercept and exception cannot be changes, " +
                    "util ByteBuddy is not used.")
    @Test
    public void testWhenNotPrepared() throws Exception {

        final String expected = "Hello world";
        final String argument = "hello";

        expect(StaticService.say(argument)).andReturn(expected);
        replay(StaticService.class);


    }
}
