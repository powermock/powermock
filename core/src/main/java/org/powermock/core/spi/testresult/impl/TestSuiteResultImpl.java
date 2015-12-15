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
package org.powermock.core.spi.testresult.impl;

import org.powermock.core.spi.testresult.Result;
import org.powermock.core.spi.testresult.TestSuiteResult;

public class TestSuiteResultImpl implements TestSuiteResult {

	private int failureCount;

	private int successCount;

	private int testCount;

	private int ignoreCount;

	public TestSuiteResultImpl() {
	}

	public TestSuiteResultImpl(int failureCount, int successCount, int testCount, int ignoreCount) {
		this.failureCount = failureCount;
		this.successCount = successCount;
		this.testCount = testCount;
		this.ignoreCount = ignoreCount;
	}

	@Override
	public int getFailureCount() {
		return failureCount;
	}

	@Override
	public int getSuccessCount() {
		return successCount;
	}

	@Override
	public int getIgnoreCount() {
		return ignoreCount;
	}

	@Override
	public Result getResult() {
		Result result = Result.SUCCESSFUL;
		if (testCount == 0) {
			result = Result.IGNORED;
		} else if (failureCount != 0) {
			result = Result.FAILED;
		}
		return result;
	}

	@Override
	public int getTestCount() {
		return testCount;
	}
	
	@Override
	public String toString() {
		return getResult().toString();
	}
}
