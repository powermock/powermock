/*
 * Copyright 2015 the original author or authors.
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

/**
 * To verify that annotation {@link PowerMockRunnerDelegate} works OK when the
 * test-class has a super-class that will be loaded from the same class-loader.
 * (This used to make JUnit's default runner fail with
 * java.lang.IllegalArgumentException: Test class can only have one constructor
 * during initialization.)
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate
public class SubclassDelegateTest extends SuperClass {

    @Test
    public void test() {}
}
