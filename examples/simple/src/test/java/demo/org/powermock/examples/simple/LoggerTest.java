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
package demo.org.powermock.examples.simple;

import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.suppressConstructor;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { Logger.class })
public class LoggerTest {

    @Test(expected = IllegalStateException.class)
    public void testException() throws Exception {
        expectNew(FileWriter.class, "target/logger.log").andThrow(new IOException());

        replayAll();
        new Logger();
    }

    @Test
    public void testLogger() throws Exception {
        PrintWriter printWriter = createMock(PrintWriter.class);
        printWriter.println("qwe");
        expectNew(PrintWriter.class, new Class[] { Writer.class }, EasyMock.anyObject()).andReturn(printWriter);
        replayAll();
        Logger logger = new Logger();
        logger.log("qwe");
        verifyAll();
    }

    @Test
    public void testLogger2() throws Exception {
        PrintWriter printWriter = createMock(PrintWriter.class);
        printWriter.println("qwe");
        suppressConstructor(Logger.class);
        replayAll();
        Logger logger = new Logger();
        Whitebox.setInternalState(logger, printWriter);
        logger.log("qwe");
        verifyAll();
    }
}
