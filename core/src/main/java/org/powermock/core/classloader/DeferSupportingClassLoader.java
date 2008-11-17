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
package org.powermock.core.classloader;

import java.util.HashMap;
import java.util.Map;

/**
 * Defers classloading of system classes to a delegate.
 * 
 * @author Johan Haleby
 * @author Jan Kronquist
 */
public abstract class DeferSupportingClassLoader extends ClassLoader {
	private Map<String, Class<?>> classes;

	private String ignoredPackages[];

	ClassLoader deferTo;

	static int count;

	public String[] getIgnoredPackages() {
		return ignoredPackages;
	}

	public DeferSupportingClassLoader(ClassLoader classloader, String ignoredPackages[]) {
		if (classloader == null) {
			deferTo = ClassLoader.getSystemClassLoader();
		} else {
			deferTo = classloader;
		}
		classes = new HashMap<String, Class<?>>();
		this.ignoredPackages = ignoredPackages;
	}

	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> clazz = null;
		if ((clazz = (Class<?>) classes.get(name)) == null) {
			if (shouldIgnore(ignoredPackages, name)) {
				clazz = deferTo.loadClass(name);
			} else {
				clazz = loadModifiedClass(name);
			}
			if (resolve) {
				resolveClass(clazz);
			}
		}

		classes.put(name, clazz);
		return clazz;
	}

	protected boolean shouldIgnore(String[] packages, String name) {
		for (String ignore : packages) {
			if(ignoreConditionMatches(name, ignore)) {
				return true;
			}
		}
		return false;
	}

	private boolean ignoreConditionMatches(String name, String ignore) {
		if ((name.startsWith(ignore) || name.endsWith(ignore)) && !(name.startsWith(ignore) && shouldModifyClass(name))) {
			return true;
		}
		return false;
	}

	protected boolean shouldIgnore(Iterable<String> packages, String name) {
		synchronized (packages) {
			for (String ignore : packages) {
				if (name.startsWith(ignore) || name.endsWith(ignore)) {
					return true;
				}
			}
		}
		return false;
	}

	protected abstract Class<?> loadModifiedClass(String s) throws ClassFormatError, ClassNotFoundException;

	protected abstract boolean shouldModifyClass(String s);

}
