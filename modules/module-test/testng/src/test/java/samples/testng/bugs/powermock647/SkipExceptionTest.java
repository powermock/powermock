package samples.testng.bugs.powermock647;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

/**
 *
 */
@PrepareForTest(SomeClass.class)
public class SkipExceptionTest extends PowerMockTestCase{

    @Test
    public void testSkipException() throws Throwable {
        new SomeClass().throwSkipException();
    }
}
