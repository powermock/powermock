/*
 * Copyright 2011-2013 the original author or authors.
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
package powermock.modules.test.mockito.junit4.delegate.parameterized;

import java.util.Arrays;
import java.util.Collection;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import samples.suppressconstructor.SuppressConstructorHierarchy;
import samples.suppresseverything.SuppressEverything;
import samples.suppressfield.SuppressField;
import samples.suppressmethod.SuppressMethod;
import samples.suppressfield.DomainObject;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

/**
 * Demonstrates PowerMock's ability to modify member structures.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({SuppressMethod.class, SuppressField.class, SuppressEverything.class})
public class SupressMethodExampleTest {

    enum GetObjectSuppression {

        DONT_SUPPRESS(SuppressMethod.OBJECT),
        SUPPRESS(null) {
                    @Override
                    void doIt() {
                        suppress(method(SuppressMethod.class, "getObject"));
                    }
                };

        final Object expectedReturnValue;

        GetObjectSuppression(Object expectedReturnValue) {
            this.expectedReturnValue = expectedReturnValue;
        }

        void doIt() {
        }
    }

    enum GetIntSuppression {

        DONT_SUPPRESS(Integer.MAX_VALUE),
        SUPPRESS(0) {
                    @Override
                    void doIt() {
                        suppress(method(SuppressMethod.class, "getInt"));
                    }
                };

        final int expectedReturnValue;

        GetIntSuppression(int expectedReturnValue) {
            this.expectedReturnValue = expectedReturnValue;
        }

        void doIt() {
        }
    }

    enum FieldSuppression {

        DONT_SUPPRESS(instanceOf(DomainObject.class)),
        SUPPRESS(nullValue()) {
                    @Override
                    void doIt() {
                        suppress(field(SuppressField.class, "domainObject"));
                    }
                };

        final Matcher<? super DomainObject> expectation;

        private FieldSuppression(Matcher<? super DomainObject> expectation) {
            this.expectation = expectation;
        }

        void doIt() {
        }
    }

    final GetObjectSuppression getObjectSuppression;
    final GetIntSuppression getIntSuppression;
    final FieldSuppression fieldSuppression;
    final boolean suppressConstructor;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    public SupressMethodExampleTest(
            GetObjectSuppression getObjectSuppression,
            GetIntSuppression getIntSuppression,
            FieldSuppression fieldSuppression,
            Boolean suppressConstructor) {
        this.getObjectSuppression = getObjectSuppression;
        this.getIntSuppression = getIntSuppression;
        this.fieldSuppression = fieldSuppression;
        this.suppressConstructor = suppressConstructor;
    }

    @Parameterized.Parameters(name = "getObject={0}  getInt={1}  field={2}  suppressConstructor={3}")
    public static Collection<?> suppressionParamValues() {
        return Arrays.asList(new Object[][]{
            {GetObjectSuppression.DONT_SUPPRESS, GetIntSuppression.DONT_SUPPRESS,
                FieldSuppression.DONT_SUPPRESS, false},
            {GetObjectSuppression.DONT_SUPPRESS, GetIntSuppression.SUPPRESS,
                FieldSuppression.DONT_SUPPRESS, false},
            {GetObjectSuppression.SUPPRESS, GetIntSuppression.DONT_SUPPRESS,
                FieldSuppression.DONT_SUPPRESS, true},
            {GetObjectSuppression.SUPPRESS, GetIntSuppression.SUPPRESS,
                FieldSuppression.DONT_SUPPRESS, true},
            {GetObjectSuppression.DONT_SUPPRESS, GetIntSuppression.DONT_SUPPRESS,
                FieldSuppression.SUPPRESS, true},
            {GetObjectSuppression.DONT_SUPPRESS, GetIntSuppression.SUPPRESS,
                FieldSuppression.SUPPRESS, true},
            {GetObjectSuppression.SUPPRESS, GetIntSuppression.DONT_SUPPRESS,
                FieldSuppression.SUPPRESS, false},
            {GetObjectSuppression.SUPPRESS, GetIntSuppression.SUPPRESS,
                FieldSuppression.SUPPRESS, false},});
    }

    @Test
    public void verifySuppression() throws Exception {
        getObjectSuppression.doIt();
        getIntSuppression.doIt();
        fieldSuppression.doIt();

        assertEquals("getObject return-value",
                getObjectSuppression.expectedReturnValue,
                new SuppressMethod().getObject());
        assertEquals("getInt return-value",
                getIntSuppression.expectedReturnValue,
                new SuppressMethod().getInt());
        assertThat("Value from field",
                new SuppressField().getDomainObject(),
                is(fieldSuppression.expectation));

        if (suppressConstructor) {
            suppress(constructor(SuppressConstructorHierarchy.class));
        } else {
            expectedException.expect(RuntimeException.class);
        }
        SuppressConstructorHierarchy tested = new SuppressConstructorHierarchy("message");
        assertTrue("Or a runtime exception should have been thrown by now", suppressConstructor);

        assertEquals(42, tested.getNumber());
        assertNull(tested.getMessage());
    }
}
