/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.powermock.api.mockito.internal.verification;

import org.mockito.internal.verification.MockAwareVerificationMode;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.invocation.MockHandler;
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
 * In PowerMock we don't know the class when calling verifyStatic().
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
    public void verify(VerificationData data) {
        super.verify(data);
    }

    @Override
    public Object getMock() {
        return clsMock;
    }
}
