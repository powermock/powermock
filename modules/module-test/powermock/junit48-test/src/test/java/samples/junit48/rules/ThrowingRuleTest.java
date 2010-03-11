/*
 * Copyright 2009 the original author or authors.
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
package samples.junit48.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Asserts that expected expectation rules also works with the PowerMock JUnit
 * 4.7 runner. Asserts that <a
 * href="http://code.google.com/p/powermock/issues/detail?id=179">issue 179</a>
 * has been resolved. Thanks to Andrei Ivanov for finding this bug.
 */
@RunWith(PowerMockRunner.class)
public class ThrowingRuleTest {
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

    @Test(expected = NullPointerException.class)
    public void unexpectAssertionErrorFailsTestCorrectly() {
        throw new NullPointerException("What happened?");
    }
}
