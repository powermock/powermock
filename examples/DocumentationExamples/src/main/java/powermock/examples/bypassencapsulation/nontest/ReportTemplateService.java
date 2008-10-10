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
package powermock.examples.bypassencapsulation.nontest;

/**
 * A stub service used to demonstrate test key-points.
 */
public class ReportTemplateService {

	/**
	 * Get a template id from a report id.
	 * 
	 * @param reportId
	 *            The id of the report whose template to get.
	 * @return The Id of the template associated with the report.
	 */
	public String getTemplateId(String reportId) {
		return null;
	}

}
