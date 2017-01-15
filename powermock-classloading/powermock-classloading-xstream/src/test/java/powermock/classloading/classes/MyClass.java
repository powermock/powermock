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
package powermock.classloading.classes;

public class MyClass {

    private final MyReturnValue myReturnValue;

    public MyClass(MyReturnValue myReturnValue) {
        this.myReturnValue = myReturnValue;
    }

    public MyReturnValue[] myMethod(MyArgument myArgument) {
        MyReturnValue[] returnValues = new MyReturnValue[2];
        returnValues[0] = myReturnValue;
        returnValues[1] = new MyReturnValue(myArgument); 
        return returnValues;
    }
}