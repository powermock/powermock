/**
 * Powermockito: withAnyArguments
 * when moving from version 1.6.5 to 1.6.6 all tests fail which are using the withAnyArguments inside
 * of "whenNew". Proxy objects aren't created any more and stay null which results in NullPointerException
 * https://github.com/jayway/powermock/issues/716
 */
package samples.powermockito.junit4.bugs.github716;