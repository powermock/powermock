/*
 * Copyright 2014 the original author or authors.
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
package samples.powermockito.junit4.bugs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TestClass.class)
public class ClassLoaderBugTest {

    /**
     * See issue <a href="https://code.google.com/p/powermock/issues/detail?id=426">426</a> for more details.
     */
    @Test(timeout = 2000)
    public void resourcesAreNotLoadedTwice() throws IOException {
    	String resourceName = getClass().getCanonicalName().replace(".", "/") + ".class";
        Enumeration<URL> enumeration = getClass().getClassLoader().getResources(resourceName);

        int count = 0;
        while (enumeration.hasMoreElements()) {
            System.out.println(enumeration.nextElement().toString());
            count++;
        }

        assertEquals(1, count);
    }

}

class TestClass {

}
