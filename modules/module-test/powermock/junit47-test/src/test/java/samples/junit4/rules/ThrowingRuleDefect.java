package samples.junit4.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ThrowingRuleDefect {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void throwsNullPointerException() {
        thrown.expect(RuntimeException.class);
        throw new RuntimeException();
    }

    @Test
    public void throwsNullPointerExceptionWithMessage() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("What happened?");
        throw new NullPointerException("What happened?");
    }

    @Test
    public void unexpectAssertionErrorFailsTestCorrectly() {
        throw new NullPointerException("What happened?");
    }
}
