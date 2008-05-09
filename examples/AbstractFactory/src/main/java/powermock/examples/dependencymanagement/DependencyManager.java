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
package powermock.examples.dependencymanagement;

import powermock.examples.service.MyService;
import powermock.examples.service.impl.MyServiceImpl;

/**
 * This is a simple example of a factory class that provides dependencies that
 * are shared by many classes (dependency lookup pattern). This approach is
 * quite common when dependency injection is not used.
 */
public final class DependencyManager {

	private static final DependencyManager instance = new DependencyManager();

	private MyService myService;

	private DependencyManager() {
	}

	public static DependencyManager getInstance() {
		return instance;
	}

	public synchronized MyService getMyService() {
		if (myService == null) {
			myService = new MyServiceImpl();
		}
		return myService;
	}
}
