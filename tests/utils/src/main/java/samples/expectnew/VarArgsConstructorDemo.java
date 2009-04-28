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
package samples.expectnew;

import samples.Service;

public class VarArgsConstructorDemo {

	private final String[] strings;
	private final Service[] services;
	private final byte[][] byteArrays;

	public VarArgsConstructorDemo(String... strings) {
		this.strings = strings;
		services = new Service[0];
		byteArrays = new byte[0][0];
	}

	public VarArgsConstructorDemo(byte[]... byteArrays) {
		this.byteArrays = byteArrays;
		services = new Service[0];
		strings = new String[0];
	}

	public VarArgsConstructorDemo(Service... services) {
		this.services = services;
		strings = new String[0];
		byteArrays = new byte[0][0];
	}

	public String[] getAllMessages() {
		return strings;
	}

	public Service[] getAllServices() {
		return services;
	}

	public byte[][] getByteArrays() {
		return byteArrays;
	}
}
