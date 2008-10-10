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

import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.expectLastCall;
import static org.powermock.PowerMock.expectPrivate;
import static org.powermock.PowerMock.createPartialMock;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import powermock.examples.bypassencapsulation.nontest.DistributedCache;
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
		ReportDao tested = createPartialMock(ReportDao.class,
				getReportFromTargetNameMethodName);

		// Create a mock of the distributed cache.
		DistributedCache distributedCacheMock = createMock(DistributedCache.class);

		/*
		 * Now that we have a mock of the distributed cache we need to set this
		 * instance in the class being tested. Notice that we use the four
		 * parameter version of setInternalState to do this. The last parameter,
		 * ReportDao.class, tells PowerMock to set the variable at a specific
		 * point in the class hierarchy. Since we've created a partial mock of
		 * the ReportDao class (since we'd wanted to mock the
		 * getReportFromTargetName method) the class under test is now actually
		 * a sub-class of ReportDao. This is because EasyMock creates a dynamic
		 * CGLib proxy which extends the ReportDao at run-time. So if we don't
		 * specify the ReportDao.class as the point in the class hierarchy where
		 * the cache field is located PowerMock will end up trying to looking
		 * for the cache field in the CGLib generated proxy where it'll
		 * obviously not find it.
		 */
		Whitebox.setInternalState(tested, "cache", distributedCacheMock,
				ReportDao.class);

		/*
		 * Create an expectation for the private method
		 * "getReportFromTargetName".
		 */
		expectPrivate(tested, getReportFromTargetNameMethodName, reportName)
				.andReturn(report);

		// Expect the call to invalidate cache.
		distributedCacheMock.invalidateCache(report);
		expectLastCall().once();

		replay(tested, distributedCacheMock);

		tested.deleteReport(reportName);

		verify(tested, distributedCacheMock);
	}
}
