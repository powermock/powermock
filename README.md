![PowerMock](powermock.png)

[![Build Status](https://travis-ci.org/jayway/powermock.svg)](https://travis-ci.org/jayway/powermock)[![Analytics](https://ga-beacon.appspot.com/UA-65548306-1/jayway/powermock)](https://github.com/jayway/powermock)

Writing unit tests can be hard and sometimes good design has to be sacrificed for the sole purpose of testability. Often testability corresponds to good design, but this is not always the case. For example final classes and methods cannot be used, private methods sometimes need to be protected or unnecessarily moved to a collaborator, static methods should be avoided completely and so on simply because of the limitations of existing frameworks.

PowerMock is a framework that extend other mock libraries such as EasyMock with more powerful capabilities. PowerMock uses a custom classloader and bytecode manipulation to enable mocking of static methods, constructors, final classes and methods, private methods, removal of static initializers and more. By using a custom classloader no changes need to be done to the IDE or continuous integration servers which simplifies adoption. Developers familiar with the supported mock frameworks will find PowerMock easy to use, since the entire expectation API is the same, both for static methods and constructors. PowerMock aims to extend the existing API's with a small number of methods and annotations to enable the extra features. Currently PowerMock supports EasyMock and Mockito.

When writing unit tests it is often useful to bypass encapsulation and therefore PowerMock includes several features that simplifies reflection specifically useful for testing. This allows easy access to internal state, but also simplifies partial and private mocking.

Please note that PowerMock is mainly intended for people with expert knowledge in unit testing. Putting it in the hands of junior developers may cause more harm than good.

## News
* 2015-03-16: PowerMock 1.6.2 has been released. See [change log](https://raw.githubusercontent.com/jayway/powermock/master/changelog.txt) for details.
* 2015-01-03: PowerMock 1.6.1 has been released with support for JUnit 4.12. See [change log](https://raw.githubusercontent.com/jayway/powermock/master/changelog.txt) for details.
* 2014-11-29: Johan blogs about the new ability to delegate to other JUnit Runners in PowerMock 1.6.0 at the [Jayway Blog](http://www.jayway.com/2014/11/29/using-another-junit-runner-with-powermock/).

[Older News](https://github.com/jayway/powermock/wiki/OldNews)

## Documentation
* [Getting Started](https://github.com/jayway/powermock/wiki/GettingStarted)
* [Downloads](Downloads)
* [EasyMock](EasyMock)
  * [Mock Static](MockStatic)
  * [Mock Final](MockFinal)
  * [Mock Private](MockPrivate)
  * [Mock New](MockConstructor)
  * [Mock Partial](MockPartial)
  * [Replay and verify all](ReplayAllAndVerifyAll)
* [Mockito](MockitoUsage) ([Legacy](MockitoUsage_Legacy))
* Common
  * [Bypass Encapsulation](BypassEncapsulation)
  * [Suppress Unwanted Behavior](SuppressUnwantedBehavior)
  * [Test Listeners](TestListeners)
  * [Mock Policies](MockPolicies)
  * [Mock system classes](MockSystem)
* [TestNG](TestNG_usage)
* [Delegate to another JUnit Runner](JUnit_Delegating_Runner)
* [Tutorial](PowerMock_tutorial)
* Experimental
  * [Bootstrap using a JUnit Rule](PowerMockRule)
  * [Bootstrap using a Java Agent](PowerMockAgent)
* [OSGi](osgi)
* [Release Notes](ReleaseNotes)
* [FAQ](FAQ) 

## Support
Join the mailing-list [here](http://groups.google.com/group/powermock) for questions, feedback and support.
