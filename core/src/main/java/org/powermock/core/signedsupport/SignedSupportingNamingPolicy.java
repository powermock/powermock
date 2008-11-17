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
package org.powermock.core.signedsupport;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

/**
 * A CGLib naming policy that supports signed classes.
 */
public final class SignedSupportingNamingPolicy extends DefaultNamingPolicy {

	private static final String PREFIX = "powermock.";

	static class SignedSupportingNamingPolicyHolder {
		static final SignedSupportingNamingPolicy SINGLETON = new SignedSupportingNamingPolicy();
	}

	private SignedSupportingNamingPolicy() {
	}

	public static NamingPolicy getInstance() {
		return SignedSupportingNamingPolicyHolder.SINGLETON;
	}

	public String getClassName(String prefix, String source, Object key, Predicate names) {
		if (prefix.startsWith("org.powermock") || prefix.startsWith("javax.") || prefix.startsWith("java.")) {
			return super.getClassName(prefix, source, key, names);
		}
		return super.getClassName(PREFIX + prefix, source, key, names);
	}
}
