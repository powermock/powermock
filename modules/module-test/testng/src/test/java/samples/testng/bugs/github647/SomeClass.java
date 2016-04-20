package samples.testng.bugs.github647;

import org.testng.SkipException;

/**
 *
 */
public class SomeClass {
    public void throwSkipException() {
        throw new SkipException("Skip test");
    }
}
