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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

/**
 * A base class that may be inherited by request creators that implement
 * {@link IRequestCreator} to create xml requests (that should be parsed to
 * byte[] data).
 */
public abstract class AbstractXMLRequestCreatorBase {

	/**
	 * Convert a dom4j xml document to a byte[].
	 * 
	 * @param document
	 *            The document to convert.
	 * @return A <code>byte[]</code> representation of the xml document.
	 * @throws IOException
	 *             If an exception occurs when converting the document.
	 */
	public byte[] convertDocumentToByteArray(Document document)
			throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		XMLWriter writer = new XMLWriter(stream);
		byte[] documentAsByteArray = null;
		try {
			writer.write(document);
		} finally {
			writer.close();
			stream.flush();
			stream.close();
		}
		documentAsByteArray = stream.toByteArray();
		return documentAsByteArray;
	}

	/**
	 * {@inheritDoc}
	 */
	public final byte[] createRequest(String... params) {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement(XMLProtocol.ENCODE_ELEMENT);
		Element header = root.addElement(XMLProtocol.HEADER_ELEMENT);
		header.addAttribute(XMLProtocol.HEADER_MSG_ID_ATTRIBUTE,
				generateRandomId());
		Element body = root.addElement(XMLProtocol.BODY_ELEMENT);
		createBody(body, params);
		byte[] array = null;
		try {
			array = convertDocumentToByteArray(document);
		} catch (IOException e) {
			throw new RuntimeException("Failed to create request", e);
		}
		return array;
	}

	/**
	 * Subclasses should implement this method to add the body content of the
	 * request.
	 * 
	 * @param body
	 *            The message body. Subclasses should append new elements to
	 *            this body.
	 * 
	 * @param parameters
	 *            Parameters that may be used when creating the request, for
	 *            example the ID of the request. May be <code>null</code> if
	 *            that's appropriate for a specific request creator.
	 */
	protected abstract void createBody(Element body, String... parameters);

	/**
	 * Generates a random <code>int</code> between 0 and 999. This
	 * <code>int</code> is then converted to a String which is returned.
	 * 
	 * @return A new String of the generated <code>int</code>.
	 */
	String generateRandomId() {
		return Integer.toString(new Random(System.nanoTime()).nextInt(1000));
	}
}
