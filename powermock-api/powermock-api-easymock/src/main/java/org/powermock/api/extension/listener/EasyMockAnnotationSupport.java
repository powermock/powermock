/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.api.extension.listener;

import org.powermock.api.easymock.EasyMockConfiguration;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.api.easymock.annotation.MockNice;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.api.extension.InjectFieldSearcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * This class works like as {@link org.easymock.EasyMockSupport} and is used to create and inject mocks to
 * annotated fields of an instance of test class.
 *
 * @see Mock
 * @see org.easymock.Mock
 * @see org.easymock.TestSubject
 */
@SuppressWarnings({"WeakerAccess", "JavadocReference"})
public class EasyMockAnnotationSupport {

    private final Object testInstance;
    private final AnnotationMockCreatorFactory annotationMockCreatorFactory;
    private final AnnotationGlobalMetadata globalMetadata;
    private final EasyMockConfiguration easyMockConfiguration;

    public EasyMockAnnotationSupport(Object testInstance) {
        this.testInstance = testInstance;
        this.annotationMockCreatorFactory = new AnnotationMockCreatorFactory();
        this.globalMetadata = new AnnotationGlobalMetadata();
        this.easyMockConfiguration = EasyMockConfiguration.getConfiguration();
    }

    public void injectMocks() throws Exception {
        injectStrictMocks();
        injectNiceMocks();
        injectDefaultMocks();
        injectTestSubjectMocks();
    }

    protected void injectStrictMocks() throws Exception {
        inject(testInstance, MockStrict.class, annotationMockCreatorFactory.createStrictMockCreator());
    }

    protected void injectNiceMocks() throws Exception {
        inject(testInstance, MockNice.class, annotationMockCreatorFactory.createNiceMockCreator());
    }

    @SuppressWarnings("deprecation")
    protected void injectDefaultMocks() throws Exception {
        inject(testInstance, Mock.class, annotationMockCreatorFactory.createDefaultMockCreator());
    }

    @SuppressWarnings("unchecked")
    protected void injectTestSubjectMocks() throws IllegalAccessException {
        if (easyMockConfiguration.isTestSubjectSupported()) {
            TestSubjectInjector testSubjectInjector = new TestSubjectInjector(testInstance, globalMetadata);
            testSubjectInjector.injectTestSubjectMocks();
        }
    }


    protected void inject(Object injectCandidateInstance, Class<? extends Annotation> annotation, AnnotationMockCreator mockCreator) throws Exception {

        AnnotationMockScanner scanner = new AnnotationMockScanner(annotation);

        List<MockMetadata> mocksMetadata = scanner.scan(injectCandidateInstance);
        globalMetadata.add(mocksMetadata);

        for (MockMetadata mockMetadata : mocksMetadata) {
            injectMock(injectCandidateInstance, mockMetadata, mockCreator, new DefaultInjectFieldSearcher());
        }


    }

    protected void injectMock(Object injectCandidateInstance, MockMetadata mockMetadata,
                              AnnotationMockCreator mockCreator, InjectFieldSearcher fieldSearch) throws IllegalAccessException {
        Object mock = createMock(mockCreator, mockMetadata);
        Field field = fieldSearch.findField(injectCandidateInstance, mockMetadata);
        if (field != null && mock != null) {
            field.setAccessible(true);
            field.set(injectCandidateInstance, mock);
        }
    }


    protected Object createMock(AnnotationMockCreator mockCreator, MockMetadata mockMetadata) {
        if (mockMetadata.getMock() == null) {
            Object mock = mockCreator.createMockInstance(mockMetadata.getType(), mockMetadata.getMethods());
            mockMetadata.setMock(mock);
        }
        return mockMetadata.getMock();
    }


}