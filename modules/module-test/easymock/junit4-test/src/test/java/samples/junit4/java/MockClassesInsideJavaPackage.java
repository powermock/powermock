/*
 * Copyright 2010 the original author or authors.
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
package samples.junit4.java;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.java.ClassInsideJavaPackage;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClassInsideJavaPackage.class)
public class MockClassesInsideJavaPackage {

    @Test
    public void allowsMockingOfClassesInsidePackageContainingJava() {
        ClassInsideJavaPackage mock = createMock(ClassInsideJavaPackage.class);
        expect(mock.mockMe()).andReturn("mocked");

        replayAll();

        assertEquals("mocked", mock.mockMe());

        verifyAll();
    }
}
