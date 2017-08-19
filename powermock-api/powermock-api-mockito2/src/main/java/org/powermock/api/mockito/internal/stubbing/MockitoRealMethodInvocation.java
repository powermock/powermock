/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.api.mockito.internal.stubbing;


public class MockitoRealMethodInvocation {
    private static final ThreadLocal<Boolean> handledByMockito = new ThreadLocal<Boolean>();
    
    private MockitoRealMethodInvocation() {
    }
    
    public static void mockitoInvocationStarted() {
        handledByMockito.set(true);
    }
    
    public static void mockitoInvocationFinished() {
        handledByMockito.set(false);
    }
    
    public static boolean isHandledByMockito() {
        final Boolean handled = handledByMockito.get();
        return handled == null ? false : handled;
    }
}
