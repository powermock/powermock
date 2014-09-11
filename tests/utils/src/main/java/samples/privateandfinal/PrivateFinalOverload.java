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
package samples.privateandfinal;

/**
 * A class used to test the functionality of capturing arguments when methods are overloaded and private and final.
 *
 * @author Johan Haleby
 */
public class PrivateFinalOverload {
    public String say(String name) {
        return say("Hello", name);
    }

    private final String say(String prefix, String name) {
        return prefix + " " + name;
    }
}
