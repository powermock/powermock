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
package samples.partialmocking;

public class MockSelfDemo {

	private int hello;

	@SuppressWarnings("unused")
	private MockSelfDemo() {
		hello = 42;
	}

	public MockSelfDemo(int hello) {
		this.hello = hello;
	}

	public MockSelfDemo(Object string) {
		this.hello = 4;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hello;
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
		final MockSelfDemo other = (MockSelfDemo) obj;
		if (hello != other.hello)
			return false;
		return true;
	}

	public String aMethod() {
		aMethod2();
		return getString("world");
	}

	public void aMethod2() {

	}

	public String getTwoStrings() {
		return getString() + getString("world2");
	}

	private String getString() {
		return "A String";
	}

	public String getString(String string) {
		return "Hello " + string;
	}

	public String getString2(String string) {
		return "Hello " + string;
	}

	public String getString2() {
		return "Hello world";
	}

	public int timesTwo(Integer anInt) {
		return anInt * 2;
	}

	public int timesTwo(int anInt) {
		return anInt * 2;
	}

	public int timesThree(int anInt) {
		return anInt * 3;
	}
	
	public int getConstructorValue() {
		return hello;
	}
}
