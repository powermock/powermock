/*
 * Copyright 2011 the original author or authors.
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
package org.powermock.core.classloader;

public class HardToTransform {
	public void run() {
		Collaborator collaborator = new Collaborator();
		for (int indx=0; indx<10; indx++) {
			collaborator.doStuff(indx);
		}
	}
	public int testInt() {
		return 5;
	}
	public double testDouble() {
		return 5;
	}
	public float testFloat() {
		return 5;
	}
	public long testLong() {
		return 5;
	}
	public short testShort() {
		return 5;
	}
	public byte testByte() {
		return 5;
	}
	public boolean testBoolean() {
		return true;
	}
	public char testChar() {
		return '5';
	}
	
	public String testString() {
		return "5";
	}
}
