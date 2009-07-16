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
package examples.jdom;

import java.io.StringReader;

import org.easymock.classextension.EasyMock;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Thanks to Manuel Fern�ndez S�nchez de la Blanca for this example.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { StaticClass.class })
@PowerMockIgnore({"org.jdom", "com.sun.org.apache.xerces"})
public class StaticWithJdomTest {

	@org.junit.Test
	public void test() throws Exception {

		PowerMock.mockStatic(StaticClass.class);
		EasyMock.expect(StaticClass.staticMethod()).andReturn(2).anyTimes();
		PowerMock.replay(StaticClass.class);

		int i = StaticClass.staticMethod();

		String xml = "<xml>" + i + "</xml>";
		SAXBuilder b = new SAXBuilder();

		Document d = b.build(new StringReader(xml));
		Assert.assertTrue(d.getRootElement().getText().equals("2"));
		PowerMock.verify(StaticClass.class);
	}
}
