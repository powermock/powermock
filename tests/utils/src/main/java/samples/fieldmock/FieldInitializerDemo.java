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
package samples.fieldmock;

import java.util.HashMap;
import java.util.Map;

public class FieldInitializerDemo {

	private Map<Integer, String> map = new HashMap<Integer, String>();

	private static Map<Integer, String> staticMap = new HashMap<Integer, String>();

	private static Map<Integer, String> staticMap2;
	static {
		staticMap2 = new HashMap<Integer, String>();
		System.out.println("### Static println!");
	}

	public String getFromMap(int index) {
		System.out.println("getFromMap class = " + map.getClass());
		System.out.println("staticMap = " + staticMap);
		System.out.println("staticMap2 = " + staticMap2);
		return map.get(index);
	}

}
