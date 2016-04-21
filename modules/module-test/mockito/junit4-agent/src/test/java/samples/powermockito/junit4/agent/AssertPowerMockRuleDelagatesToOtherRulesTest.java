/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package samples.powermockito.junit4.agent;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestName;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This test demonstrates that the PowerMockRule delegates to other rules.
 */
public class AssertPowerMockRuleDelagatesToOtherRulesTest {
	private static final MyObject BEFORE = new MyObject();

	private final List<Object> objects = new LinkedList<Object>();

	@Rule
	public PowerMockRule powerMockRule = new PowerMockRule();

	@Rule
	public MyRule rule = new MyRule();

	@Rule
	public TestName testName = new TestName();

	@Test
	public void assertPowerMockRuleDelegatesToOtherRules() throws Exception {
		assertFalse(this.getClass().getClassLoader().getClass().getName().contains(MockClassLoader.class.getName()));
		assertEquals(1, objects.size());
        // Not same using X-Stream
		assertEquals(BEFORE, objects.get(0));
		assertEquals("assertPowerMockRuleDelegatesToOtherRules", testName.getMethodName());
	}

	private class MyRule implements MethodRule {
		@Override
		public Statement apply(final Statement base, FrameworkMethod method, Object target) {
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					objects.add(BEFORE);
					base.evaluate();
				}
			};
		}
	}

    private static class MyObject {
        private final String state = "state";

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyObject myObject = (MyObject) o;

	        return state != null ? state.equals(myObject.state) : myObject.state == null;
        }

        @Override
        public int hashCode() {
            return state.hashCode();
        }
    }
}
