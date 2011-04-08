/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.modules.agent;

import java.io.*;
import java.lang.management.*;
import java.util.*;

import com.sun.tools.attach.*;
import com.sun.tools.attach.spi.*;
import sun.tools.attach.*;

final class JDK6AgentLoader {
   private static final AttachProvider ATTACH_PROVIDER = new AttachProvider()  {
      @Override
      public String name() { return null; }

      @Override
      public String type() { return null; }

      @Override
      public VirtualMachine attachVirtualMachine(String id) { return null; }

      @Override
      public List<VirtualMachineDescriptor> listVirtualMachines() { return null; }
   };

   private final String jarFilePath;
   private final String pid;

   JDK6AgentLoader(String jarFilePath) {
      this.jarFilePath = jarFilePath;
      pid = discoverProcessIdForRunningVM();
   }

   private String discoverProcessIdForRunningVM() {
      String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
      int p = nameOfRunningVM.indexOf('@');

      return nameOfRunningVM.substring(0, p);
   }

   void loadAgent() {
      VirtualMachine vm;

      if (AttachProvider.providers().isEmpty()) {
         vm = getVirtualMachineImplementationFromEmbeddedOnes();
      }
      else {
         vm = attachToThisVM();
      }

      loadAgentAndDetachFromThisVM(vm);
   }

   @SuppressWarnings({"UseOfSunClasses"})
   private VirtualMachine getVirtualMachineImplementationFromEmbeddedOnes()  {
      try {
         if (File.separatorChar == '\\') {
            return new WindowsVirtualMachine(ATTACH_PROVIDER, pid);
         }
         else {
            return new LinuxVirtualMachine(ATTACH_PROVIDER, pid);
         }
      }
      catch (AttachNotSupportedException e) {
         throw new RuntimeException(e);
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
      catch (UnsatisfiedLinkError ignore) {
         //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
         throw new IllegalStateException(
            "Unable to load Java agent; please add lib/tools.jar from your JDK to the classpath");
      }
   }

   private VirtualMachine attachToThisVM() {
      try {
         return VirtualMachine.attach(pid);
      }
      catch (AttachNotSupportedException e) {
         throw new RuntimeException(e);
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private void loadAgentAndDetachFromThisVM(VirtualMachine vm) {
      try {
         vm.loadAgent(jarFilePath, null);
         vm.detach();
      }
      catch (AgentLoadException e) {
         throw new RuntimeException(e);
      }
      catch (AgentInitializationException e) {
         throw new RuntimeException(e);
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
