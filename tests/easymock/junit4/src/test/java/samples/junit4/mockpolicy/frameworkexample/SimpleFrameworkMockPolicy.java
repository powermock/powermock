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
package samples.junit4.mockpolicy.frameworkexample;

import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;
import org.powermock.mockpolicies.MockPolicyInterceptionSettings;
import org.powermock.reflect.Whitebox;
import samples.mockpolicy.frameworkexample.NativeResult;
import samples.mockpolicy.frameworkexample.SimpleFramework;

import java.lang.reflect.Method;

public class SimpleFrameworkMockPolicy implements PowerMockPolicy {

	public static final String NATIVE_RESULT_VALUE = "result";

	@Override
	public void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings) {
		settings.addStaticInitializersToSuppress("samples.mockpolicy.frameworkexample.SimpleFramework");
	}

	@Override
	public void applyInterceptionPolicy(MockPolicyInterceptionSettings settings) {
		final Method doNativeStuffMethod = Whitebox.getMethod(SimpleFramework.class, String.class);
		NativeResult nativeResult = new NativeResult(NATIVE_RESULT_VALUE);
		settings.stubMethod(doNativeStuffMethod, nativeResult);
	}
}
