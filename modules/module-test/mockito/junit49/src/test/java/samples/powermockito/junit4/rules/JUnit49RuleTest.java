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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.MethodRule;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.LinkedList;
import java.util.List;

/**
 * JUnit 4.9 changed the implementation/execution of Rules.
 * Demonstrates that <a
 * href="http://code.google.com/p/powermock/issues/detail?id=344">issue 344</a>
 * is resolved.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { JUnit49RuleTest.class })
public class JUnit49RuleTest {

    private static Object BEFORE = new Object();

    private List<Object> objects = new LinkedList<Object>();

    @Rule
    public MyRule rule = new MyRule();

    @Rule
    public TestName testName = new TestName();

    @Test
    public void assertThatJUnit47RulesWorks() throws Exception {
        assertEquals(1, objects.size());
        assertSame(BEFORE, objects.get(0));
        assertEquals("assertThatJUnit47RulesWorks", testName.getMethodName());
    }

    private class MyRule implements TestRule {

        public Statement apply(final Statement statement, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    objects.add(BEFORE);
                    statement.evaluate();
                }
            };
        }
    }
}