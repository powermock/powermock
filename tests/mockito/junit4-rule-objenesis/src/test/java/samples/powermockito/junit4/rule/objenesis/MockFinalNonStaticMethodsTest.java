/*
 * Copyright 2011 the original author or authors.
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
package samples.powermockito.junit4.rule.objenesis;

import org.junit.Rule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.finalmocking.FinalDemo;
import samples.powermockito.junit4.finalmocking.MockFinalMethodsCases;
import samples.privateandfinal.PrivateFinal;

/**
 * Test class to demonstrate non-static final mocking with Mockito.
 */
@PrepareForTest( { FinalDemo.class, PrivateFinal.class })
public class MockFinalNonStaticMethodsTest extends MockFinalMethodsCases {
    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();
}
