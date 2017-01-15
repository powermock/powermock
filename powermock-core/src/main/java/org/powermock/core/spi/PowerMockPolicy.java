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
package org.powermock.core.spi;

import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;
import org.powermock.mockpolicies.MockPolicyInterceptionSettings;

/**
 * This interface can be implemented to create policies for certain frameworks
 * to make it easier for users to test their code in isolation from these
 * frameworks. A mock policy implementation can for example suppress some
 * methods, suppress static initializers or intercept method calls and change
 * their return value (for example to return a mock object). <i>A mock policy
 * implementation must be stateless</i>. The reason why there are two methods
 * for applying settings is that PowerMock needs to know which classes that
 * should be modified by the mock class loader <i>before</i> these classes have
 * loaded. The {@link #applyClassLoadingPolicy(MockPolicyClassLoadingSettings)}
 * tells PowerMock which classes that should be loaded and then the
 * {@link #applyInterceptionPolicy(MockPolicyInterceptionSettings)} is called
 * from the mock class-loader itself. This means you can create mocks for e.g.
 * final and static methods in the
 * {@link #applyInterceptionPolicy(MockPolicyInterceptionSettings)} which would
 * not have been possible otherwise.
 * <p>
 * Since mock policies can be chained subsequent policies can override behavior
 * of a previous policy. To avoid accidental overrides it's recommended
 * <i>add</i> behavior instead of <i>setting</i> behavior since the latter
 * overrides all previous configurations.
 */
public interface PowerMockPolicy {

	/**
	 * Apply all class-loading related policies that must be present before the
	 * interception policies can take place.
	 * 
	 * @param settings
	 *            The settings objects where the class-loading policies can be
	 *            applied.
	 */
	void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings);

	/**
	 * Apply the interception policies, for example which methods that should be
	 * suppressed or which methods that should be intercepted and return some
	 * else than their original value.
	 * 
	 * @param settings
	 *            The settings objects where the interception policies can be
	 *            applied.
	 */
	void applyInterceptionPolicy(MockPolicyInterceptionSettings settings);
}
