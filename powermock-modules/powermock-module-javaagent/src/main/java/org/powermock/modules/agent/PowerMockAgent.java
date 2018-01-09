/*
 * The JMockit Testing Toolkit
 * Copyright (c) 2006-2011 Rogério Liesenfeld
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.powermock.modules.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

/**
 * This is the "agent class" that initializes the PowerMock "Java agent". It is not intended for use in client code.
 * It must be public, however, so the JVM can call the {@code premain} method, which as the name implies is called
 * <em>before</em> the {@code main} method.
 *
 * @see #premain(String, Instrumentation)
 */
public final class PowerMockAgent
{

	static final String javaSpecVersion = System.getProperty("java.specification.version");
    static final boolean jdk6OrLater = "1.6".equals(javaSpecVersion) || "1.7".equals(javaSpecVersion) || "1.8".equals(javaSpecVersion)
	    || "9".equals(javaSpecVersion);

    private static final PowerMockClassTransformer classTransformer = new PowerMockClassTransformer();
    
    private static Instrumentation instrumentation;

    private PowerMockAgent() {}

    public static boolean isJava6OrLater() { return jdk6OrLater; }

    /**
     * This method must only be called by the JVM, to provide the instrumentation object.
     * In order for this to occur, the JVM must be started with "-javaagent:powermock-module-javaagent-nnn.jar" as a command line parameter
     * (assuming the jar file is in the current directory).
     *
     */
    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        initialize(agentArgs, inst);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        initialize(agentArgs, inst);
    }

    private static void initialize(String agentArgs, Instrumentation inst) throws IOException {
        instrumentation = inst;
        inst.addTransformer(new DefinalizingClassTransformer(), false);
        inst.addTransformer(classTransformer, true);
    }
    
    public static PowerMockClassTransformer getClasstransformer() {
		return classTransformer;
	}

    public static Instrumentation instrumentation()  {
        verifyInitialization();
        return instrumentation;
    }

    public static void verifyInitialization()
    {
        if (instrumentation == null) {
            new AgentInitialization().initializeAccordingToJDKVersion();
        }
    }

    public static boolean initializeIfNeeded()
    {
        if (instrumentation == null) {
            try {
                new AgentInitialization().initializeAccordingToJDKVersion();
                return true;
            }
            catch (RuntimeException e) {
                e.printStackTrace(); // makes sure the exception gets printed at least once
                throw e;
            }
        }

        return false;
    }

    public static void initializeIfPossible() {
        if (jdk6OrLater) {
            initializeIfNeeded();
        }
    }
}
