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
package demo.org.powermock.examples.tutorial.hellopower.withoutpowermock;

import demo.org.powermock.examples.tutorial.hellopower.SimpleConfig;

/**
 * Encapsulate the use of the third party code in our own code that we can mock. 
 */
public class ConfigWrapper {
	public String getTarget() {
		return SimpleConfig.getTarget();
	}

	public String getGreeting() {
		return SimpleConfig.getGreeting();
	}
}
