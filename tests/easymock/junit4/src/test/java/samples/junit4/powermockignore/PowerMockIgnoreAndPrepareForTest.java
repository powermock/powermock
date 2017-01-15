package samples.junit4.powermockignore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.singleton.StaticService;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("samples.*")
@PrepareForTest(StaticService.class)
public class PowerMockIgnoreAndPrepareForTest {

    @Test public void
    powermock_ignore_annotation_and_prepare_for_test_annotation_can_be_combined() {
        mockStatic(StaticService.class);
        String expected = "qwe";
        expect(StaticService.doStatic(5)).andReturn(expected);
        replay(StaticService.class);

        String actual = StaticService.doStatic(5);
        assertEquals(expected, actual);
        verify(StaticService.class);
    }
}
