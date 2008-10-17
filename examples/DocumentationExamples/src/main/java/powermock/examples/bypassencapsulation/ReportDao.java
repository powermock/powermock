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

import powermock.examples.bypassencapsulation.nontest.Cache;
import powermock.examples.bypassencapsulation.nontest.Injectable;
import powermock.examples.bypassencapsulation.nontest.Report;

/**
 * A class used to demonstrate how it's possible for PowerMock to set internal
 * state for a class that is also partially mocked.
 */
public class ReportDao {

	@Injectable
	private Cache cache;

	public void deleteReport(final String reportName) {
		Report report = getReportFromTargetName(reportName);
		cache.invalidateCache(report);
		// Imagine that we delete the report from some persistence storage.
	}

	private Report getReportFromTargetName(final String reportName) {
		/* Imagine that this method does something that many methods share */
		return null;
	}
}
