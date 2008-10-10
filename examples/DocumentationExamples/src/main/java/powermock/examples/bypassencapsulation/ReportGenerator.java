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

import powermock.examples.bypassencapsulation.nontest.Report;
import powermock.examples.bypassencapsulation.nontest.ReportTemplateService;
import powermock.examples.bypassencapsulation.nontest.Injectable;

@SuppressWarnings("unused")
public class ReportGenerator {

	@Injectable
	private ReportTemplateService reportTemplateService;

	public Report generateReport(String reportId) {
		String templateId = reportTemplateService.getTemplateId(reportId);
		/*
		 * Imagine some other code here that generates the report based on the
		 * template id.
		 */
		return new Report("name");
	}
}
