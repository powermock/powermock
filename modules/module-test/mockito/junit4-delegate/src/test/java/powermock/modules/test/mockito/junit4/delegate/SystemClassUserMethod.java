/*
 * Copyright 2014 the original author or authors.
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class SystemClassUserMethod {

    private final Method method;

    public SystemClassUserMethod(Method method) {
        this.method = method;
    }

    public static SystemClassUserMethod[] values() {
        Method[] methods = SystemClassUserCases.class.getDeclaredMethods();
        List<SystemClassUserMethod> values =
                new ArrayList<SystemClassUserMethod>(methods.length);
        for (Method m : methods) {
            if (m.isAnnotationPresent(Test.class)) {
                values.add(new SystemClassUserMethod(m));
            }
        }
        return values.toArray(new SystemClassUserMethod[values.size()]);
    }

    @Override
    public String toString() {
        return method.getName();
    }

    public Method getMethod() {
        return method;
    }
}
