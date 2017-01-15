/*
 * Copyright 2005 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package com.sun.tools.attach;

/**
 * The exception thrown when an agent cannot be loaded into the target Java virtual machine.
 * <p/>
 * <p> This exception is thrown by {@link
 * VirtualMachine#loadAgent VirtualMachine.loadAgent} or
 * {@link VirtualMachine#loadAgentLibrary
 * VirtualMachine.loadAgentLibrary}, {@link VirtualMachine#loadAgentPath loadAgentPath} methods
 * if the agent, or agent library, cannot be loaded.
 */
public final class AgentLoadException extends Exception
{
   private static final long serialVersionUID = 688047862952114238L;

   /**
    * Constructs an {@code AgentLoadException} with
    * no detail message.
    */
   public AgentLoadException()
   {
   }

   /**
    * Constructs an {@code AgentLoadException} with the specified detail message.
    */
   public AgentLoadException(String s)
   {
      super(s);
   }
}
