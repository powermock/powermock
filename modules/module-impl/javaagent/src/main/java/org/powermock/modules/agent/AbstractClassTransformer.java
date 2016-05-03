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

package org.powermock.modules.agent;

import org.powermock.core.WildcardMatcher;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractClassTransformer {
    
    private static final List<String> ALWAYS_IGNORED = new LinkedList<String>();
    private final List<String> USER_IGNORED = Collections.synchronizedList(new LinkedList<String>());

    static {
        ALWAYS_IGNORED.add("org.powermock.*");
        ALWAYS_IGNORED.add("org.junit.*");
        ALWAYS_IGNORED.add("org.testng.*");
        ALWAYS_IGNORED.add("org.assertj.*");
        ALWAYS_IGNORED.add("org.mockito.*");
        ALWAYS_IGNORED.add("javassist.*");
        ALWAYS_IGNORED.add("org.objenesis.*");
        ALWAYS_IGNORED.add("junit.*");
        ALWAYS_IGNORED.add("org.hamcrest.*");
        ALWAYS_IGNORED.add("sun.*");
        ALWAYS_IGNORED.add("$Proxy*");
        ALWAYS_IGNORED.add("*CGLIB$$*");
        ALWAYS_IGNORED.add("*$$PowerMock*");
    }     

    public synchronized void setPackagesToIgnore(List<String> packagesToIgnore) {
        USER_IGNORED.clear();
        USER_IGNORED.addAll(packagesToIgnore);
    }

    public void resetPackagesToIgnore() {
        USER_IGNORED.clear();
    }

    protected boolean shouldIgnore(String className) {
        return WildcardMatcher.matchesAny(merge(USER_IGNORED), replaceSlashWithDots(className));
    }

    private List<String> merge(List<String> userIgnored) {
        List<String> list = new LinkedList<String>(AbstractClassTransformer.ALWAYS_IGNORED);
        list.addAll(userIgnored);
        return Collections.unmodifiableList(list);
    }

    String replaceSlashWithDots(String className) {
        return className.replaceAll("/", ".");
    }
}
