package samples.testng.bugs.powermock647;

import org.testng.SkipException;

/**
 *
 */
public class SomeClass {
    public void throwSkipException() {
        throw new SkipException("Skip test");
    }
}
