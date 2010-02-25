/*
 * Copyright 2010 the original author or authors.
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
package samples.overloading;

import samples.classhierarchy.ChildA;
import samples.classhierarchy.Parent;

/**
 * Demonstrates that PowerMock correctly methods that seam to be overloaded but
 * differ because one is static and one is instance.
 */
public class StaticAndInstanceMethodWithSameNameUser {

    public void performInstaceInvocation(StaticAndInstanceMethodWithSameName object) {
        Parent child = new ChildA();
        object.overloaded(child);
    }

    public void performStaticInvocation() {
        Parent child = new ChildA();
        StaticAndInstanceMethodWithSameName.overloaded((ChildA) child);
    }
}
