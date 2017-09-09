/*
 * Copyright 2008 the original author or authors.
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
package org.powermock.api.extension.listener;

import org.powermock.api.easymock.EasyMockConfiguration;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.api.easymock.annotation.MockNice;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.core.spi.listener.AnnotationEnablerListener;
import org.powermock.core.spi.support.AbstractPowerMockTestListenerBase;
import org.powermock.reflect.Whitebox;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * <p>
 * Before each test method all fields annotated with
 * {@link Mock}, {@link org.powermock.api.easymock.annotation.Mock}, {@link org.easymock.Mock}
 * {@link MockNice} or {@link MockStrict} will have mock objects created for
 * them and injected to the fields.
 * </p>
 * <p>
 * Also all fields annotated with {@link org.easymock.TestSubject} will be processed and mocks are injected to fields
 * object, if these fields not null.
 * </p>
 * <p>
 * It will only inject to fields that haven't been set before (i.e that are
 * {@code null}).
 * </p>
 *
 * @see org.powermock.api.easymock.annotation.Mock
 * @see org.easymock.Mock
 * @see org.easymock.TestSubject
 *
 */
@SuppressWarnings({"deprecation", "JavadocReference"})
public class AnnotationEnabler extends AbstractPowerMockTestListenerBase implements AnnotationEnablerListener {


    @SuppressWarnings("unchecked")
    public Class<? extends Annotation>[] getMockAnnotations() {
        return new Class[]{Mock.class, MockNice.class, MockStrict.class};
    }

    @Override
    public void beforeTestMethod(Object testInstance, Method method, Object[] arguments) throws Exception {

        EasyMockConfiguration easyMockConfiguration = EasyMockConfiguration.getConfiguration();

        if (!easyMockConfiguration.isReallyEasyMock()) {
            // Easymock API could be used as depends for JMock.
            return;
        }

        // first emulate default EasyMockRunner behavior
        if (easyMockConfiguration.isInjectMocksSupported()) {
            Whitebox.invokeMethod(Class.forName("org.easymock.EasyMockSupport"), "injectMocks", testInstance);
        }

        // then inject in empty fields mock created via PowerMock

        getEasyMockAnnotationSupport(testInstance).injectMocks();
    }

    @SuppressWarnings("WeakerAccess")
    protected EasyMockAnnotationSupport getEasyMockAnnotationSupport(Object testInstance) {
        return new EasyMockAnnotationSupport(testInstance);
    }
}
