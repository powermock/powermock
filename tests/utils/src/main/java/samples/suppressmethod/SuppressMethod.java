/*
 * Copyright 2008 the original author or authors.
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
package samples.suppressmethod;

public class SuppressMethod extends SuppressMethodParent {

    public static final Object OBJECT = new Object();

    public Object getObjectWithArgument(String argument) {
        return OBJECT;
    }

    public Object getObject() {
        return OBJECT;
    }

    public static Object getObjectStatic() {
        return OBJECT;
    }

    public byte getByte() {
        return Byte.MAX_VALUE;
    }

    public short getShort() {
        return Short.MAX_VALUE;
    }

    public int getInt() {
        return Integer.MAX_VALUE;
    }

    public long getLong() {
        return Long.MAX_VALUE;
    }

    public boolean getBoolean() {
        return true;
    }

    public float getFloat() {
        return Float.MAX_VALUE;
    }

    public double getDouble() {
        return Double.MAX_VALUE;
    }

    public double getDouble(double value) {
        return value;
    }

    public void invokeVoid(StringBuilder s) {
        s.append("This should be suppressed!");
    }

    @Override
    public int myMethod() {
        return 20 + super.myMethod();
    }

    public Object sameName(int i) {
        return OBJECT;
    }

    public Object sameName(float f) {
        return OBJECT;
    }
}
