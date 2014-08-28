/*
 * Copyright 2014 the original author or authors.
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

package demo.org.powermock.examples;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SimpleOps.class)
public class SimpleOpsTest {

    /**
     * Asserts that <a href="https://code.google.com/p/powermock/issues/detail?id=513">issue 513</a> is resolved.
     */
    @Test public void
    can_call_methods_using_default_methods() {
        SimpleOps ops = new SimpleOps();
        ops.doStreamStuff(Collections.emptyList());
    }
}
