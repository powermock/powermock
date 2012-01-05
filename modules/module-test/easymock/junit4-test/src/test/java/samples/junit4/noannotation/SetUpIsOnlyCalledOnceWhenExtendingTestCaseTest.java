package samples.junit4.noannotation;

import junit.framework.TestCase;
import org.junit.Before;

public class SetUpIsOnlyCalledOnceWhenExtendingTestCaseTest extends TestCase {

    private int state = 0;

    @Before
    @Override
    public void setUp() throws Exception {
        state++;
    }

    public void testSetupMethodIsOnlyCalledOnceWhenExtendingFromTestCase() throws Exception {
        assertEquals(1, state);
    }
}
