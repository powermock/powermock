/*
 * Copyright 2008 the original author or authors.
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
package samples.powermockito.junit4.rules;

import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * JUnit 4.9 changed the implementation/execution of Rules.
 * Demonstrates that <a
 * href="http://code.google.com/p/powermock/issues/detail?id=344">issue 344</a>
 * is resolved.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { JUnit49RuleTest.class })
public class JUnit49RuleTest {

    @Rule
    public ExpectedException expected = ExpectedException.none();

   
    @Test
    public void usingRuleAnnotationWorks() {
        assertTrue(true);
    }

}