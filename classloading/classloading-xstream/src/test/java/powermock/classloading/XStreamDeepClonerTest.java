/*
 * Copyright 2010 the original author or authors.
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

package powermock.classloading;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.classloading.DeepCloner;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class XStreamDeepClonerTest {

    @Test
    public void clonesJavaInstances() throws Exception {
        final URL original = new URL("http://www.powermock.org");
        URL clone = new DeepCloner().clone(original);
        assertEquals(clone, original);
        assertNotSame(clone, original);
    }

    @Test
    public void clonesUnmodifiableLists() throws Exception {
        final UnmodifiableListExample original = new UnmodifiableListExample();
        UnmodifiableListExample clone = new DeepCloner().clone(original);
        assertEquals(clone, original);
        assertNotSame(clone, original);
    }

    @Test
    public void clonesArraysWithNullValues() throws Exception {
        Object[] original = new Object[] { "Test", null };
        Object[] clone = new DeepCloner().clone(original);
        assertArrayEquals(clone, original);
        assertNotSame(clone, original);
    }

}

class UnmodifiableListExample {
    private List<NotSerializable> cl = Collections.unmodifiableList(Arrays.asList(new NotSerializable()));

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cl == null) ? 0 : cl.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        powermock.classloading.UnmodifiableListExample other = (powermock.classloading.UnmodifiableListExample) obj;
        if (cl == null) {
            if (other.cl != null)
                return false;
        } else if (!cl.equals(other.cl))
            return false;
        return true;
    }
}

class NotSerializable {
    private final String state = "Nothing";

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        powermock.classloading.NotSerializable other = (powermock.classloading.NotSerializable) obj;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        return true;
    }
}