/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.classloading;

import org.powermock.classloading.spi.DeepClonerSPI;

import java.util.concurrent.Callable;

/**
 * A ClassLoaderExecutor can run any code in any classloader. E.g. assume you have a classloader
 * called myClassloader and you want to execute a (void) method called myMethod in myObject using this CL:
 * <pre>
 *     SingleClassloaderExecutor cle = new SingleClassloaderExecutor(myClassloader);
 *     cle.execute(new Runnable() {
 *          public void run() {
 *             myObject.myMethod();
 *          }
 *     });
 * </pre>
 *
 * What happens is that the entire object graph of myObject is deep-cloned into the {@code myClassloader} classloader
 * and then the {@code myObject.myMethod()} is executed.
 * <p>
 * You can also execute methods that return something:
 * <pre>
 *     SingleClassloaderExecutor cle = new SingleClassloaderExecutor(myClassloader);
 *     MyResult result = cle.execute(new Callable<MyResult>() {
 *          public MyResult call() throws Exception {
 *             return myObject.myMethod();
 *          }
 *     });
 * </pre>
 * Here we imagine that {@code myObject.myMethod()} returns an object of type {@code MyResult}. Again the entire
 * state will be deep-cloned to  {@code myClassloader} and then the {@code myObject.myMethod()} is executed.
 * The result of the method call is deep-cloned back into the original classloader (the one that made the call to
 * {@code cle.execute(..)}) and is ready for use.
 * </p>
 * <p>
 * Note that the SingleClassloaderExecutor requires a deep cloner implementing the {@link DeepClonerSPI} present in the class-path.
 * </p>
 */
public interface ClassloaderExecutor {

    <T> T execute(Callable<T> callable);

    void execute(Runnable runnable);
}
