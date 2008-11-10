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
package powermock.examples.newmocking;

import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.createMockAndExpectNew;
import static org.powermock.PowerMock.expectNew;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit test for the {@link PersistenceManager} class that demonstrates
 * PowerMock's ability to mock new instance calls.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PersistenceManager.class)
public class PersistenceManagerTest {

	@Test
	public void testCreateDirectoryStructure_ok() throws Exception {
		final String path = "directoryPath";
		File fileMock = createMock(File.class);

		PersistenceManager tested = new PersistenceManager();

		expectNew(File.class, path).andReturn(fileMock);

		expect(fileMock.exists()).andReturn(false);
		expect(fileMock.mkdirs()).andReturn(true);

		replay(fileMock, File.class);

		assertTrue(tested.createDirectoryStructure(path));

		verify(fileMock, File.class);
	}

	@Test
	public void testCreateDirectoryStructure_usingCreateMockAndExpectNew() throws Exception {
		final String path = "directoryPath";
		File fileMock = createMockAndExpectNew(File.class, path);

		PersistenceManager tested = new PersistenceManager();

		expect(fileMock.exists()).andReturn(false);
		expect(fileMock.mkdirs()).andReturn(true);

		replay(fileMock, File.class);

		assertTrue(tested.createDirectoryStructure(path));

		verify(fileMock, File.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateDirectoryStructure_fails() throws Exception {
		final String path = "directoryPath";
		File mFileMock = createMock(File.class);

		PersistenceManager tested = new PersistenceManager();

		expectNew(File.class).andReturn(mFileMock);

		expect(mFileMock.exists()).andReturn(true);

		replay(mFileMock, File.class);

		tested.createDirectoryStructure(path);

		verify(mFileMock, File.class);
	}
}
