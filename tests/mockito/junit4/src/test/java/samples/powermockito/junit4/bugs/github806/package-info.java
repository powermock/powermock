/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

/**
 * Exception verification in junit tests #806
 * <p>
 * <code>
 * @Test(expected = NotFoundException.class)
 * public void a() {
 * doThrow(NotFoundException.class).when(b).doSomething();
 * b.doSomething();
 * }
 * </code>
 * Test failure, throws RuntimeExceptionProxy instead of NotFoundException
 * <p>
 * https://github.com/powermock/powermock/issues/806
 */
package samples.powermockito.junit4.bugs.github806;