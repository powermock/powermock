package org.powermock.tests.result.impl;

import org.powermock.tests.result.Result;
import org.powermock.tests.result.TestSuiteResult;

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

	public int getFailureCount() {
		return failureCount;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public int getIgnoreCount() {
		return ignoreCount;
	}

	public Result getResult() {
		Result result = Result.SUCCESSFUL;
		if (testCount == 0) {
			result = Result.IGNORED;
		} else if (failureCount != 0) {
			result = Result.FAILED;
		}
		return result;
	}

	public int getTestCount() {
		return testCount;
	}
	
	
}
