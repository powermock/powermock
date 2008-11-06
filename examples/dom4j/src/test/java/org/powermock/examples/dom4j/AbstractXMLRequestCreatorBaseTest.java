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
package org.powermock.examples.dom4j;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.createPartialMock;
import static org.powermock.PowerMock.mockStatic;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit test for the {@link AbstractXMLRequestCreatorBase} class.
 */
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("org.dom4j.tree.AbstractNode")
@PrepareForTest( { DocumentHelper.class })
public class AbstractXMLRequestCreatorBaseTest {
	private AbstractXMLRequestCreatorBase tested;

	private Document documentMock;

	private Element rootElementMock;

	private Element headerElementMock;

	private Element bodyElementMock;

	@Before
	public void setUp() throws Exception {
		tested = new AbstractXMLRequestCreatorBase() {
			@Override
			protected void createBody(Element body, String... parameters) {

			}
		};
		PowerMock.niceReplayAndVerify();
		documentMock = createMock(Document.class);
		rootElementMock = createMock(Element.class);
		headerElementMock = createMock(Element.class);
		bodyElementMock = createMock(Element.class);
	}

	@After
	public void tearDown() throws Exception {
		tested = null;
		documentMock = null;
		rootElementMock = null;
		headerElementMock = null;
		bodyElementMock = null;
	}

	/**
	 * Replay all mocks
	 */
	protected void replayAll() {
		replay(tested, documentMock, rootElementMock, headerElementMock, bodyElementMock);
	}

	/**
	 * Verify all mocks
	 */
	protected void verifyAll() {
		verify(tested, documentMock, rootElementMock, headerElementMock, bodyElementMock);
	}

	/**
	 * Test convert document to byte array.
	 * 
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	@Test
	@PrepareForTest
	@SuppressStaticInitializationFor
	public void testConvertDocumentToByteArray() throws Exception {
		// Create a fake document.
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("ListExecutionContexts");
		root.addAttribute("id", "2");
		replayAll();
		// Perform the test
		final byte[] array = tested.convertDocumentToByteArray(document);
		verifyAll();
		assertNotNull(array);
		assertEquals(70, array.length);
	}

	/**
	 * Happy-flow test for the
	 * {@link AbstractXMLRequestCreatorBase#createRequest(String[])} method.
	 * 
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	@Test
	public void testCreateRequest() throws Exception {
		tested = createPartialMock(AbstractXMLRequestCreatorBase.class, "convertDocumentToByteArray", "createBody", "generateRandomId");
		mockStatic(DocumentHelper.class);
		// Expectations
		final String[] params = new String[] { "String1", "String2" };
		final byte[] expected = new byte[] { 42 };

		expect(DocumentHelper.createDocument()).andReturn(documentMock);
		expect(documentMock.addElement(XMLProtocol.ENCODE_ELEMENT)).andReturn(rootElementMock);
		expect(rootElementMock.addElement(XMLProtocol.HEADER_ELEMENT)).andReturn(headerElementMock);
		final String id = "213";
		expect(tested.generateRandomId()).andReturn(id);
		expect(headerElementMock.addAttribute(XMLProtocol.HEADER_MSG_ID_ATTRIBUTE, id)).andReturn(null);
		expect(rootElementMock.addElement(XMLProtocol.BODY_ELEMENT)).andReturn(bodyElementMock);
		tested.createBody(bodyElementMock, params);
		expectLastCall().times(1);
		expect(tested.convertDocumentToByteArray(documentMock)).andReturn(expected);

		replayAll();
		replay(DocumentHelper.class);

		byte[] actual = tested.createRequest(params);

		verifyAll();
		verify(DocumentHelper.class);

		assertArrayEquals(expected, actual);
	}
}
