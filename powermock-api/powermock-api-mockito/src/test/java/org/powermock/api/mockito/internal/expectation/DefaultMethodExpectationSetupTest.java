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
package org.powermock.api.mockito.internal.expectation;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;
import org.powermock.reflect.Whitebox;

/**
 * @author Stanislav Chizhov
 */
public class DefaultMethodExpectationSetupTest {

    private final CUT object = new CUT();

    @Test(expected = MissingMethodInvocationException.class)
    public void testWithArguments_Multiple() throws Exception {
        DefaultMethodExpectationSetup s = new DefaultMethodExpectationSetup(object, object.getClass().getMethod("multiple", Object.class, Object.class, Object.class));
        Object a1 = new Object();
        Object a2 = new Object();
        Object a3 = new Object();
        s.withArguments(a1, a2, a3);
    }

    @Test(expected = MissingMethodInvocationException.class)
    public void testWithArguments_Single() throws Exception {
        DefaultMethodExpectationSetup s = new DefaultMethodExpectationSetup(object, object.getClass().getMethod("single", Object.class));
        Object a1 = new Object();
        s.withArguments(a1);
    }

    @Test
    public void testJoin() throws Exception {
        Object a1 = new Object();
        Object a2 = new Object();
        Object a3 = new Object();
        Object[] res = Whitebox.invokeMethod(DefaultMethodExpectationSetup.class, "join", a1, new Object[]{a2, a3});

        Assert.assertArrayEquals(new Object[]{a1, a2, a3}, res);
    }

    public static class CUT {

        public void multiple(Object a1, Object a2, Object a3) {
            //Nada
        }

        public void single(Object a1) {
            //Nada
        }
    }
}
