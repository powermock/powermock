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
package org.powermock.api.mockito.internal.mockmaker;

import org.mockito.internal.InternalMockHandler;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.creation.bytebuddy.ByteBuddyMockMaker;
import org.mockito.internal.stubbing.InvocationContainer;
import org.mockito.internal.util.MockNameImpl;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.MockHandler;
import org.mockito.mock.MockCreationSettings;
import org.mockito.plugins.MockMaker;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.internal.pluginswitch.PowerMockPluginSwitch;
import org.powermock.api.mockito.repackaged.CglibMockMaker;
import org.powermock.core.classloader.MockClassLoader;

import java.util.List;

/**
 * A PowerMock implementation of the MockMaker. Right now it simply delegates to the default Mockito
 * {@link org.mockito.plugins.MockMaker} via {@link org.mockito.internal.configuration.plugins.Plugins#getMockMaker()}
 * but in the future we may use it more properly.
 * The reason for its existence is that the current Mockito MockMaker throws exception when getting the name
 * from of a mock that is created by PowerMock but not know for Mockito. This is triggered when by the
 * {@link org.mockito.internal.util.MockUtil} class.
 * For more details see the {@link org.powermock.api.mockito.internal.invocation.ToStringGenerator}.
 */
public class PowerMockMaker implements MockMaker {

    private static boolean isEnabled = true;
    private final MockMaker cglibMockMaker = new CglibMockMaker();
    private final MockMaker byteBuddyMockMaker = new ByteBuddyMockMaker();

    @Override
    public <T> T createMock(MockCreationSettings<T> settings, MockHandler handler) {
        if(!isEnabled) {
            return byteBuddyMockMaker.createMock(settings, handler);
        }
        T mock = cglibMockMaker.createMock(settings, handler);
        ClassLoader classLoader = cglibMockMaker.getClass().getClassLoader();
        if (classLoader instanceof MockClassLoader) {
            MockClassLoader mcl = (MockClassLoader) classLoader;
            // The generated class is not picked up by PowerMock so we cache it here
            mcl.cache(mock.getClass());
        }
        return mock;
    }

    @Override
    public MockHandler getHandler(Object mock) {
        if(!isEnabled) {
            return byteBuddyMockMaker.getHandler(mock);
        } else if (mock instanceof Class) { // Return a fake mock handler for static method mocks
            return new PowerMockInternalMockHandler((Class<?>) mock);
        } else {
            return cglibMockMaker.getHandler(mock);
        }
    }

    @Override
    public void resetMock(Object mock, MockHandler newHandler, MockCreationSettings settings) {
        if(!isEnabled) {
            byteBuddyMockMaker.resetMock(mock, newHandler, settings);
            return;
        }
        cglibMockMaker.resetMock(mock, newHandler, settings);
    }

    @Override
    public TypeMockability isTypeMockable(Class<?> type) {
        if (!isEnabled) {
            byteBuddyMockMaker.isTypeMockable(type);
        }
        return cglibMockMaker.isTypeMockable(type);
    }

    /**
     * It needs to extend InternalMockHandler because Mockito requires the type to be of InternalMockHandler and not MockHandler
     */
    private static class PowerMockInternalMockHandler implements InternalMockHandler<Object> {
        private final Class<?> mock;

        public PowerMockInternalMockHandler(Class<?> mock) {
            this.mock = mock;
        }

        @Override
        public MockCreationSettings getMockSettings() {
            final MockSettingsImpl mockSettings = new MockSettingsImpl();
            mockSettings.setMockName(new MockNameImpl(mock.getName()));
            mockSettings.setTypeToMock(mock);
            return mockSettings;
        }

        @Override
        public void setAnswersForStubbing(List<Answer<?>> list) {
        }

        @Override
        public InvocationContainer getInvocationContainer() {
            return null;
        }

        @Override
        public Object handle(Invocation invocation) throws Throwable {
            return null;
        }
    }

    /**
     * @deprecated Please use {@link PowerMockPluginSwitch#disablePowerMockMaker} instead
     */
    @Deprecated
    public static void disablePowerMockMaker() {
        isEnabled = false;
    }

    /**
     * @deprecated Please use {@link PowerMockPluginSwitch#enablePowerMockMaker} instead
     */
    @Deprecated
    public static void enablePowerMockMaker() {
        isEnabled = true;
    }
}
