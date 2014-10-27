/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package powermock.modules.test.mockito.junit4.delegate;

import org.hamcrest.Matcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import samples.staticandinstance.StaticAndInstanceDemo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.runner.Description.createTestDescription;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Demonstrates how PowerMockRunner with annotation PowerMockRunnerDelegate can
 * provide a low-level JUnit (in this case a self-contained "selfie" test)
 * with some PowerMock abilities.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SelfieTest.class)
@PrepareForTest(StaticAndInstanceDemo.class)
public class SelfieTest extends Runner {

    private static final String stubbedReturnValue
            = "Stubbed return-value from " + SelfieTest.class;

    private static final Description first = createTestDescription(
            SelfieTest.class, "No Stubbing");
    private static final Description second = createTestDescription(
            SelfieTest.class, "Stubbed Static Return-Value");

    /**
     * Mandatory Runner constructor
     */
    public SelfieTest(Class<SelfieTest> ignore) {
    }

    @Override
    public Description getDescription() {
        Description desc = Description
                .createSuiteDescription(SelfieTest.class);
        desc.addChild(first);
        desc.addChild(second);
        return desc;
    }

    @Override
    public void run(RunNotifier notifier) {
        assert_getStaticMessage(notifier, first,
                not(equalTo(stubbedReturnValue)));
        mockStatic(StaticAndInstanceDemo.class);
        when(StaticAndInstanceDemo.getStaticMessage())
                .thenReturn(stubbedReturnValue);
        assert_getStaticMessage(notifier, second, equalTo(stubbedReturnValue));
    }

    void assert_getStaticMessage(RunNotifier notifier, Description currentTest,
            Matcher<? super String> getStaticMessageExpectation) {
        notifier.fireTestStarted(currentTest);
        try {
            String staticMessage = StaticAndInstanceDemo.getStaticMessage();
            if (getStaticMessageExpectation.matches(staticMessage)) {
                notifier.fireTestFinished(currentTest);
            } else {
                notifier.fireTestFailure(new Failure(currentTest, new AssertionError(
                        "Unexpected #getStaticMessage() return-value: "
                        + staticMessage)));
            }
        } catch (Exception ex) {
            notifier.fireTestFailure(new Failure(currentTest, ex));
        }
    }
}
