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
* [Downloads](https://github.com/jayway/powermock/wiki/Downloads)
* [EasyMock](https://github.com/jayway/powermock/wiki/EasyMock)
  * [Mock Static](https://github.com/jayway/powermock/wiki/MockStatic)
  * [Mock Final](https://github.com/jayway/powermock/wiki/MockFinal)
  * [Mock Private](https://github.com/jayway/powermock/wiki/MockPrivate)
  * [Mock New](https://github.com/jayway/powermock/wiki/MockConstructor)
  * [Mock Partial](https://github.com/jayway/powermock/wiki/MockPartial)
  * [Replay and verify all](https://github.com/jayway/powermock/wiki/ReplayAllAndVerifyAll)
* [Mockito](https://github.com/jayway/powermock/wiki/MockitoUsage) ([Legacy](https://github.com/jayway/powermock/wiki/MockitoUsage_Legacy))
* Common
  * [Bypass Encapsulation](https://github.com/jayway/powermock/wiki/BypassEncapsulation)
  * [Suppress Unwanted Behavior](https://github.com/jayway/powermock/wiki/SuppressUnwantedBehavior)
  * [Test Listeners](https://github.com/jayway/powermock/wiki/TestListeners)
  * [Mock Policies](https://github.com/jayway/powermock/wiki/MockPolicies)
  * [Mock system classes](https://github.com/jayway/powermock/wiki/MockSystem)
* [TestNG](https://github.com/jayway/powermock/wiki/TestNG_usage)
* [Delegate to another JUnit Runner](https://github.com/jayway/powermock/wiki/JUnit_Delegating_Runner)
* [Tutorial](https://github.com/jayway/powermock/wiki/PowerMock_tutorial)
* Experimental
  * [Bootstrap using a JUnit Rule](https://github.com/jayway/powermock/wiki/PowerMockRule)
  * [Bootstrap using a Java Agent](https://github.com/jayway/powermock/wiki/PowerMockAgent)
* [OSGi](https://github.com/jayway/powermock/wiki/osgi)
* [Release Notes](https://github.com/jayway/powermock/wiki/ReleaseNotes)
* [FAQ](https://github.com/jayway/powermock/wiki/FAQ) 

## Support and discussion
Join the mailing-list [here](http://groups.google.com/group/powermock) for questions, feedback and support.

## Links
* [Change log](https://raw.githubusercontent.com/jayway/powermock/master/changelog.txt)
* [PowerMock on Ohloh](http://www.ohloh.net/p/powermock/)
* [Mailing list for questions and support](http://groups.google.com/group/powermock)
 
## Sponsored by:
[![JAYWAY](http://www.arctiquator.com/oppenkallkod/assets/images/jayway_logo.png)](http://www.jayway.com/)

