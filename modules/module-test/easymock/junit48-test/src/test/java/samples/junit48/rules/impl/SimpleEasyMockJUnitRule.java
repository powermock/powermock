package samples.junit48.rules.impl;

import org.easymock.IMocksControl;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import static org.easymock.EasyMock.createControl;

/**
 * A JUnit rule that resets all mocks before each test and verifies the mocks after the test
 */
public class SimpleEasyMockJUnitRule implements MethodRule {

    final IMocksControl control;
    boolean recording = true;
    public Error caughtError = null;

    /**
     * Create the rule using the default EasyMock.createControl()
     */
    public SimpleEasyMockJUnitRule() {
        this(createControl());
    }

    /**
     * Create the rule using the IMocksControl that you provide
     * 
     * @param control
     *            The provided IMocksControl to use for testing
     */
    public SimpleEasyMockJUnitRule(IMocksControl control) {
        this.control = control;
    }

    public <T> T createMock(Class<T> toMock) {
        return control.createMock(toMock);
    }

    public void reset() {
        recording = true;
        control.reset();
    }

    public void replay() {
        recording = false;
        control.replay();
    }

    public void verify() {
        control.verify();
    }

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                reset();
                base.evaluate();
                if (!recording) {
                    verify(); // only verify if no exceptions were thrown
                }
            }
        };
    }

}
