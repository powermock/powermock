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

package powermock.test.support;

import org.powermock.core.classloader.MockClassLoader;

/**
 * This class is used when running tests in {@link org.powermock.core.transformers.impl.MainMockTransformerTest}. It is
 * placed in this package because classes in org.powermock.core.* are deferred by:
 * {@link MockClassLoader#packagesToBeDeferred}. Additionally, the class must be modified when it is loaded, and as such
 * not in {@link MockClassLoader#packagesToLoadButNotModify}.
 */
public class MainMockTransformerTestSupport {
    public static class SupportClasses {
        public final static class StaticFinalInnerClass {
        }

        public final static class FinalInnerClass {
        }


        private final static class PrivateStaticFinalInnerClass {
        }

        public enum EnumClass {
            VALUE;
        }

        class SuperClass {
        }

        public class SubClass extends SuperClass {
            public void dummyMethod() {}
        }
    }
}
