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
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.mockMethod;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;
import static org.powermock.PowerMock.mockStatic;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for the {@link AbstractXMLRequestCreatorBase} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { DocumentHelper.class })
public class AbstractXMLRequestCreatorBaseTest {
	private AbstractXMLRequestCreatorBase mTested;

	private Document mDocumentMock;

	private Element mRootElementMock;

	private Element mHEADERElementMock;

	private Element mBodyElementMock;

	@Before
	public void setUp() throws Exception {
		mTested = createMock(AbstractXMLRequestCreatorBase.class, new Method[0]);
		mDocumentMock = createMock(Document.class);
		mRootElementMock = createMock(Element.class);
		mHEADERElementMock = createMock(Element.class);
		mBodyElementMock = createMock(Element.class);
		mockStatic(DocumentHelper.class);
	}

	@After
	public void tearDown() throws Exception {
		mTested = null;
		mDocumentMock = null;
		mRootElementMock = null;
		mHEADERElementMock = null;
		mBodyElementMock = null;
	}

	/**
	 * Replay all mocks
	 */
	protected void replayAll() {
		replay(mTested, mDocumentMock, mRootElementMock, mHEADERElementMock,
				mBodyElementMock);
		replay(DocumentHelper.class);
	}

	/**
	 * Verify all mocks
	 */
	protected void verifyAll() {
		verify(mTested, mDocumentMock, mRootElementMock, mHEADERElementMock,
				mBodyElementMock);
		verify(DocumentHelper.class);
	}

	/**
	 * Test convert document to byte array.
	 * 
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	@Test
	@PrepareForTest
	@Ignore
	public void testConvertDocumentToByteArray() throws Exception {
		// Create a fake document.
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("ListExecutionContexts");
		root.addAttribute("id", "2");
		replayAll();
		// Perform the test
		final byte[] array = mTested.convertDocumentToByteArray(document);
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
	@Ignore
	public void testCreateRequest() throws Exception {
		mTested = mockMethod(AbstractXMLRequestCreatorBase.class,
				"convertDocumentToByteArray", "createBody", "generateRandomId");

		// Expectations
		final String[] params = new String[] { "String1", "String2" };
		final byte[] expected = new byte[] { 42 };

		expect(DocumentHelper.createDocument()).andReturn(mDocumentMock);
		expect(mDocumentMock.addElement(XMLProtocol.ENCODE_ELEMENT)).andReturn(
				mRootElementMock);
		expect(mRootElementMock.addElement(XMLProtocol.HEADER_ELEMENT))
				.andReturn(mHEADERElementMock);
		final String id = "213";
		expect(mTested.generateRandomId()).andReturn(id);
		expect(
				mHEADERElementMock.addAttribute(
						XMLProtocol.HEADER_MSG_ID_ATTRIBUTE, id)).andReturn(
				null);
		expect(mRootElementMock.addElement(XMLProtocol.BODY_ELEMENT))
				.andReturn(mBodyElementMock);
		mTested.createBody(mBodyElementMock, params);
		expectLastCall().times(1);
		expect(mTested.convertDocumentToByteArray(mDocumentMock)).andReturn(
				expected);

		replayAll();

		byte[] actual = mTested.createRequest(params);

		verifyAll();

		assertArrayEquals(expected, actual);
	}
}
