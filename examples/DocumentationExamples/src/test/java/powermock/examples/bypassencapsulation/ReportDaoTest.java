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
package powermock.examples.bypassencapsulation;

import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.createPartialMock;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import powermock.examples.bypassencapsulation.nontest.Cache;
import powermock.examples.bypassencapsulation.nontest.Report;

/**
 * Unit tests for the {@link ReportDao} class. This demonstrates one basic usage
 * of PowerMock's ability to set internal state for a class that is partially
 * mocked.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ReportDao.class)
public class ReportDaoTest {

	@Test
	public void testDeleteReport() throws Exception {
		final String getReportFromTargetNameMethodName = "getReportFromTargetName";
		final String reportName = "reportName";
		final Report report = new Report(reportName);

		// Mock only the modifyData method
		ReportDao tested = createPartialMock(ReportDao.class, getReportFromTargetNameMethodName);

		// Create a mock of the distributed cache.
		Cache cacheMock = createMock(Cache.class);

		/*
		 * Now that we have a mock of the cache we need to set this instance in
		 * the class being tested.
		 */
		Whitebox.setInternalState(tested, "cache", cacheMock);

		/*
		 * Create an expectation for the private method
		 * "getReportFromTargetName".
		 */
		expectPrivate(tested, getReportFromTargetNameMethodName, reportName).andReturn(report);

		// Expect the call to invalidate cache.
		cacheMock.invalidateCache(report);
		expectLastCall().once();

		replay(tested, cacheMock);

		tested.deleteReport(reportName);

		verify(tested, cacheMock);
	}

	@Test
	public void testDeleteReport_usingPowerMock1Features() throws Exception {
		final String getReportFromTargetNameMethodName = "getReportFromTargetName";
		final String reportName = "reportName";
		final Report report = new Report(reportName);

		// Mock only the modifyData method
		ReportDao tested = createPartialMock(ReportDao.class, getReportFromTargetNameMethodName);

		// Create a mock of the distributed cache.
		Cache cacheMock = createMock(Cache.class);

		/*
		 * Now that we have a mock of the cache we need to set this instance in
		 * the class being tested.
		 */
		Whitebox.setInternalState(tested, cacheMock);

		/*
		 * Create an expectation for the private method
		 * "getReportFromTargetName".
		 */
		expectPrivate(tested, getReportFromTargetNameMethodName, reportName).andReturn(report);

		// Expect the call to invalidate cache.
		cacheMock.invalidateCache(report);
		expectLastCall().once();

		replayAll();

		tested.deleteReport(reportName);

		verifyAll();
	}
}
