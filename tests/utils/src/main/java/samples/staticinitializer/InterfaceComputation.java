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
package samples.staticinitializer;

interface InterfaceA
{
    public static final int A = 2 * InterfaceB.B;
//    public void methodA();
} // End of interface
interface InterfaceB
{
    public static final int B = InterfaceC.C + 1;
//    public String methodB(int qwe);
} // End of interface
interface InterfaceC extends InterfaceA
{
    public static final int C = A + 1;
} // End of interface

public class InterfaceComputation implements InterfaceA, InterfaceB, InterfaceC
{
	public static void main(String[] args) {
		System.out.println(calculateWithinHierarchy());
	}
	public static int calculateWithinHierarchy() {
		return C + B + A;
	}
	public static int calculateWithReference() {
		return InterfaceC.C + InterfaceB.B + InterfaceA.A;
	}

//	public void methodA() {
//	}
//
//	public String methodB(int qwe) {
//		return "";
//	} 
} // End of class
