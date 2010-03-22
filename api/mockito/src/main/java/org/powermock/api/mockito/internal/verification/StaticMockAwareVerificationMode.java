package org.powermock.api.mockito.internal.verification;

import org.mockito.internal.MockHandler;
import org.mockito.internal.verification.MockAwareVerificationMode;
import org.mockito.verification.VerificationMode;

/**
 * A custom extension of {@link MockAwareVerificationMode} for static method
 * verification. The reason for this implementation is that since Mockito 1.8.4
 * the verification code in Mockito
 * {@link MockHandler#handle(org.mockito.internal.invocation.Invocation)} has
 * changed and the verification mode MUST be an instance of
 * {@link MockAwareVerificationMode} for the verification to work. Since
 * verifying static methods is a two step process in PowerMock we need to be
 * able to specify the class a later state then verification start. I.e. in
 * standard Mockito they always know the mock object when doing verify before
 * calling the method to verify:
 * 
 * <pre>
 * verify(mock).methodToVerify();
 * </pre>
 * 
 * In PowerMock we don't know the clas when calling verifyStatic().
 */
public class StaticMockAwareVerificationMode extends MockAwareVerificationMode {

    private Class<?> clsMock;

    public StaticMockAwareVerificationMode(VerificationMode mode) {
        super(null, mode);
    }

    public void setClassMock(Class<?> clsMock) {
        this.clsMock = clsMock;
    }

    @Override
    public Object getMock() {
        return clsMock;
    }
}
