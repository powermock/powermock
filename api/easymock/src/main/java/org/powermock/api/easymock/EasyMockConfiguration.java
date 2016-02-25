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

package org.powermock.api.easymock;

/**
 * Configuration information about EasyMock framework and which feature is supported by version of EasyMock in runtime.
 *
 * @since 1.6.5
 */
public class EasyMockConfiguration {

    private static final EasyMockConfiguration INSTANCE = new EasyMockConfiguration();
    private boolean testSubjectSupported;
    private boolean reallyEasyMock;
    private boolean injectMocksSupported;

    private EasyMockConfiguration() {
        initTestSubjectSupported();
        initReallyEasyMock();
        initInjectMocksSupported();
    }

    public static EasyMockConfiguration getConfiguration() {
        return INSTANCE;
    }
    
    private void initTestSubjectSupported() {
        try {
            Class.forName("org.easymock.TestSubject");
            testSubjectSupported = true;
        } catch (ClassNotFoundException e) {
            testSubjectSupported = false;
        }
    }

    private void initReallyEasyMock() {
        try {
            Class.forName("org.easymock.EasyMockSupport");
            reallyEasyMock = true;
        } catch (ClassNotFoundException e) {
            reallyEasyMock = false;
        }
    }

    private void initInjectMocksSupported() {
        try {
            Class<?> clazz = Class.forName("org.easymock.EasyMockSupport");
            clazz.getDeclaredMethod("injectMocks", Object.class);
            injectMocksSupported = true;
        } catch (NoSuchMethodException e) {
            injectMocksSupported = false;
        } catch (ClassNotFoundException e) {
            injectMocksSupported = false;
        }
    }

    public boolean isInjectMocksSupported() {
        return injectMocksSupported;
    }

    public boolean isReallyEasyMock() {
        return reallyEasyMock;
    }

    public boolean isTestSubjectSupported() {
        return testSubjectSupported;
    }
}
