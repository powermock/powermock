package org.powermock.tests.result.impl;

import org.powermock.tests.result.Result;
import org.powermock.tests.result.TestMethodResult;

public class TestMethodResultImpl implements TestMethodResult {

	private final Result result;

	public TestMethodResultImpl(Result result) {
		super();
		this.result = result;
	}

	public Result getResult() {
		return result;
	}

}
