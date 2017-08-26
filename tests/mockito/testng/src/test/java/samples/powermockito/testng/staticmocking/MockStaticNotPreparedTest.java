package samples.powermockito.testng.staticmocking;

import org.powermock.api.mockito.ClassNotPreparedException;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;
import samples.singleton.StaticService;

import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class MockStaticNotPreparedTest extends PowerMockTestCase {
    
    @Test(expectedExceptions = ClassNotPreparedException.class)
    public void should_throw_exception_if_class_not_prepared_for_test() throws Exception {

        mockStatic(StaticService.class);

    }
}
