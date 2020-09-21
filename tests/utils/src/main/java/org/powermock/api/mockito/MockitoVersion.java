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

package org.powermock.api.mockito;

import java.security.CodeSource;
import java.security.ProtectionDomain;

public class MockitoVersion {
    
    public static boolean isMockito1(){
        return MOCKITO_VERSION.isMockito1_0();
    }

    public static boolean isMockito2(){
        return MOCKITO_VERSION.isMockito2_0();
    }

    public static boolean isMockito3(){
        return MOCKITO_VERSION.isMockito3_0();
    }
    
    private static final MockitoVersion MOCKITO_VERSION = new MockitoVersion();
    
    private final String version;
    
    private MockitoVersion() {
        String ver = "";
        try {
            Class<?> mockitoClass = Class.forName("org.mockito.Mock");
            ProtectionDomain protectionDomain = mockitoClass.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            String path = codeSource.getLocation().toString();
            int x = path.lastIndexOf("-");
            int y = path.lastIndexOf(".");
            ver = path.substring(x + 1, y);
        } catch (Exception e) {
            ver = "";
        } finally {
            version = ver;
        }
    }
    
    
    private boolean isMockito1_0() {
            return version.startsWith("1");
        }
    
    private boolean isMockito2_0() {
        return version.startsWith("2");
    }

    private boolean isMockito3_0() {
        return version.startsWith("3");
    }
}
