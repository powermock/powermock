/*
 * Copyright 2009 the original author or authors.
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
package samples.equalswithgetclass;

/**
 * Class that implements an equals method that contains a call to getClass();
 */
public class EqualsWithGetClass {

    private final String myString;

    public EqualsWithGetClass(String myString) {
        super();
        this.myString = myString;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((myString == null) ? 0 : myString.hashCode());
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
        EqualsWithGetClass other = (EqualsWithGetClass) obj;
        if (myString == null) {
            if (other.myString != null)
                return false;
        } else if (!myString.equals(other.myString))
            return false;
        return true;
    }
}
