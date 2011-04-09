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
package samples.interfacemethodfinding;

import org.powermock.reflect.internal.WhiteboxImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * There was a bug in PowerMock 1.2 and its predecessors that made PowerMock
 * {@link WhiteboxImpl#getMethod(Class, Class...)} fail when invoking proxified
 * interface methods declared in extended interfaces. E.g. if interface A
 * extends B & C and a method was declared in B it wouldn't be found by
 * {@link WhiteboxImpl#getMethod(Class, Class...)} since it only used to
 * traverse the class hierarchy and not the structure of the extended
 * interfaces. This was fixed in version 1.3 and this class is used to
 * demonstrate the issue.
 * <p>
 * Thanks to Lokesh Vaddi for finding this bug and to provide an example.
 */
public class InterfaceMethodHierarchyUsage {

	public void usePreparedStatement() throws Exception {
		Connection conn = null;
		// PreparedStatement extends other interfaces, this is the point of
		// interest.
		PreparedStatement prepared = null;
		try {
			conn = WsUtil.getConnection();
			prepared = conn.prepareStatement("select * from emp");
		} catch (Exception e) {
			System.out.println(e);

		} finally {
			conn.close();
			prepared.close();
		}
	}
}
