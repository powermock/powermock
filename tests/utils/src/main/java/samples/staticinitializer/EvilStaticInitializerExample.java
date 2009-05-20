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
package samples.staticinitializer;

/**
 * Simple example of a class with a static initializer.
 */
public class EvilStaticInitializerExample {

	public static final String FAILED_TO_LOAD_LIBRARY_MESSAGE = "Failed to load a required dll, please make sure that you've installed the software correctly";

	static {
		try {
			System.loadLibrary("path/to/mylibrary.dll");
		} catch (UnsatisfiedLinkError error) {
			throw new UnsatisfiedLinkError(FAILED_TO_LOAD_LIBRARY_MESSAGE);
		}
	}

	/*
	 * We imagine that this method require the library to execute, but we want
	 * to test it anyway in seperation.
	 */
	public String doSomeNativeStuffUsingTheLoadedSystemLibrary() {
		return "native stuff";
	}
}
