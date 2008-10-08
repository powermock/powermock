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
package powermock.examples.privatemocking;

/**
 * A class used to demonstrate how it's possible for PowerMock to mock private
 * methods invocations.
 */
public class DataService {
	
	

	public boolean replaceData(final String dataId, final byte[] binaryData) {
		return modifyData(dataId, binaryData);
	}

	public boolean deleteData(final String dataId) {
		return modifyData(dataId, null);
	}

	/**
	 * Modify the data.
	 * 
	 * @param dataId
	 *            The ID of the data slot where the binary data will be stored.
	 * @param binaryData
	 *            The binary data that will be stored. If <code>null</code>
	 *            this means that the data for the particular slot is considered
	 *            removed.
	 * @return <code>true</code> if the operation was successful,
	 *         <code>false</code> otherwise.
	 */
	private boolean modifyData(final String dataId, final byte[] binaryData) {
		/*
		 * Imagine this method doing something complex and expensive.
		 */
		return true;
	}
}
