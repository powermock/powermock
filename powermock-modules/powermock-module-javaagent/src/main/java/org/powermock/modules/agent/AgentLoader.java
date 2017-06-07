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

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.attach.spi.AttachProvider;
import org.powermock.reflect.Whitebox;
import sun.tools.attach.BsdVirtualMachine;
import sun.tools.attach.LinuxVirtualMachine;
import sun.tools.attach.SolarisVirtualMachine;
import sun.tools.attach.WindowsVirtualMachine;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

final class AgentLoader {
    private static final AttachProvider ATTACH_PROVIDER = new AttachProvider() {
        @Override
        public String name() {
            return null;
        }

        @Override
        public String type() {
            return null;
        }

        @Override
        public VirtualMachine attachVirtualMachine(String id) {
            return null;
        }

        @Override
        public List<VirtualMachineDescriptor> listVirtualMachines() {
            return null;
        }
    };

    private final String jarFilePath;
    private final String pid;

    AgentLoader(String jarFilePath) {
        this.jarFilePath = jarFilePath;
        pid = discoverProcessIdForRunningVM();
    }

    private static String discoverProcessIdForRunningVM() {
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');

        return nameOfRunningVM.substring(0, p);
    }

    boolean loadAgent() {
        VirtualMachine vm;

        if (AttachProvider.providers().isEmpty()) {
            vm = getVirtualMachineImplementationFromEmbeddedOnes();
        } else {
            vm = attachToThisVM();
        }

        if (vm != null) {
            loadAgentAndDetachFromThisVM(vm);
            return true;
        }

        return false;
    }

    @SuppressWarnings("UseOfSunClasses")
    private VirtualMachine getVirtualMachineImplementationFromEmbeddedOnes() {
        try {
            Class<? extends VirtualMachine> vmClass;

            if (File.separatorChar == '\\') {
                vmClass = WindowsVirtualMachine.class;
            } else {
                String osName = System.getProperty("os.name");

                if (osName.startsWith("Linux") || osName.startsWith("LINUX")) {
                    vmClass = LinuxVirtualMachine.class;
                } else if (osName.startsWith("Mac OS X")) {
                    vmClass = BsdVirtualMachine.class;
                } else if (osName.startsWith("Solaris")) {
                    vmClass = SolarisVirtualMachine.class;
                } else {
                    return null;
                }
            }

            // This is only done with Reflection to avoid the JVM pre-loading all the XyzVirtualMachine classes.
            Class<?>[] parameterTypes = {AttachProvider.class, String.class};

            VirtualMachine newVM = null;
            try {
                newVM = Whitebox.invokeConstructor(vmClass, parameterTypes, new Object[]{ATTACH_PROVIDER, pid});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return newVM;
        } catch (UnsatisfiedLinkError e) {
            throw new IllegalStateException("Native library for Attach API not available in this JRE", e);
        }
    }

    private VirtualMachine attachToThisVM() {
        try {
            return VirtualMachine.attach(pid);
        } catch (AttachNotSupportedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAgentAndDetachFromThisVM(VirtualMachine vm) {
        try {
            vm.loadAgent(jarFilePath, null);
            vm.detach();
        } catch (AgentLoadException e) {
            throw new RuntimeException(e);
        } catch (AgentInitializationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
