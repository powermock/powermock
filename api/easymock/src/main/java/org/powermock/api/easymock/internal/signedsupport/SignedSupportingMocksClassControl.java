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
package org.powermock.api.easymock.internal.signedsupport;

import org.easymock.internal.IProxyFactory;
import org.easymock.internal.MocksControl;

/**
 * An extension of the {@link MocksClassControl} that loads the
 * {@link SignedSupportingClassProxyFactory} to allow mocking of signed class
 * files.
 */
public class SignedSupportingMocksClassControl extends MocksControl {

	public SignedSupportingMocksClassControl(MockType type) {
		super(type);
	}

	private static final long serialVersionUID = 1427365963088516775L;

	@Override
	protected <T> IProxyFactory<T> createProxyFactory(Class<T> toMock) {
		if (toMock.isInterface()) {
			return super.createProxyFactory(toMock);
		}
		return new SignedSupportingClassProxyFactory<T>();
	}
}
