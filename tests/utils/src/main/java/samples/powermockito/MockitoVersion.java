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

package samples.powermockito;

public class MockitoVersion {

    private static final SystemPropertiesMockitoVersion MOCKITO_VERSION = new SystemPropertiesMockitoVersion();


    public static boolean isMockito1(){
        return MOCKITO_VERSION.isMockito1();
    }

    public static boolean isMockito2(){
        return MOCKITO_VERSION.isMockito2();
    }


    private static class SystemPropertiesMockitoVersion  {
        private final String version;

        private  SystemPropertiesMockitoVersion(){
            version = System.getProperty("mockitoVersion");
        }

        private boolean isMockito1(){
            return version.startsWith("1");
        }

        public boolean isMockito2() {
            return version.startsWith("2");
        }
    }
}
