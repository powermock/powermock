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

package samples.powermockito.junit4.bugs.github801;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class GlobalPowerMockIgnoreTest {
    
    @Test
    public void should_load_class_from_global_ignore_with_system_class_loader() {
        GlobalPowerMockIgnore globalPowerMockIgnore = new GlobalPowerMockIgnore();
        
        assertThat(globalPowerMockIgnore.getClass().getClassLoader())
            .as("Class is loaded by System Classloader")
            .isSameAs(ClassLoader.getSystemClassLoader());
    }
    
}
