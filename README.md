![PowerMock](powermock.png)

[![Build Status](https://travis-ci.org/jayway/powermock.svg)](https://travis-ci.org/jayway/powermock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.powermock/powermock/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.powermock/powermock)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.powermock/powermock/badge.svg)](http://www.javadoc.io/doc/org.powermock/powermock)
[![Dependency Status](https://www.versioneye.com/user/projects/56da744cbfdb330030419e61/badge.svg?style=flat)](https://www.versioneye.com/user/projects/56da744cbfdb330030419e61)

Usage status: Mockito: [![Reference Status](https://www.versioneye.com/java/org.powermock:powermock-api-mockito/reference_badge.svg?style=flat)](https://www.versioneye.com/java/org.powermock:powermock-api-mockito/references) Easymock: [![Reference Status](https://www.versioneye.com/java/org.powermock:powermock-api-easymock/reference_badge.svg?style=flat)](https://www.versioneye.com/java/org.powermock:powermock-api-easymock/references) jUnit: [![Reference Status](https://www.versioneye.com/java/org.powermock:powermock-module-junit4/reference_badge.svg?style=flat)](https://www.versioneye.com/java/org.powermock:powermock-module-junit4/references) TestNG [![Reference Status](https://www.versioneye.com/java/org.powermock:powermock-module-testng/reference_badge.svg?style=flat)](https://www.versioneye.com/java/org.powermock:powermock-module-testng/references)

Writing unit tests can be hard and sometimes good design has to be sacrificed for the sole purpose of testability. Often testability corresponds to good design, but this is not always the case. For example final classes and methods cannot be used, private methods sometimes need to be protected or unnecessarily moved to a collaborator, static methods should be avoided completely and so on simply because of the limitations of existing frameworks.

PowerMock is a framework that extends other mock libraries such as EasyMock with more powerful capabilities. PowerMock uses a custom classloader and bytecode manipulation to enable mocking of static methods, constructors, final classes and methods, private methods, removal of static initializers and more. By using a custom classloader no changes need to be done to the IDE or continuous integration servers which simplifies adoption. Developers familiar with the supported mock frameworks will find PowerMock easy to use, since the entire expectation API is the same, both for static methods and constructors. PowerMock aims to extend the existing API's with a small number of methods and annotations to enable the extra features. Currently PowerMock supports EasyMock and Mockito.

When writing unit tests it is often useful to bypass encapsulation and therefore PowerMock includes several features that simplifies reflection specifically useful for testing. This allows easy access to internal state, but also simplifies partial and private mocking.

Please note that PowerMock is mainly intended for people with expert knowledge in unit testing. Putting it in the hands of junior developers may cause more harm than good.

## News
* 2015-12-11: PowerMock 1.6.4 has been released with better support for [Jacoco](http://eclemma.org/jacoco/) and improvements to the [DelegatingPowerMockRunner](https://github.com/jayway/powermock/wiki/JUnit_Delegating_Runner) as well as some other minor fixes. See [change log](https://raw.githubusercontent.com/jayway/powermock/master/changelog.txt) for details.
* 2015-10-02: PowerMock 1.6.3 has been released with support for EasyMock 3.4 as well as compatibility with ByteBuddy and various other fixes. See [change log](https://raw.githubusercontent.com/jayway/powermock/master/changelog.txt) for details.
* 2015-07-25: PowerMock has moved to GitHub. From now on the old <a href="https://code.google.com/p/powermock/">Google Code page</a> should not be used anymore. All issues are reported here on GitHub and the documentation is migrated.

[Older News](https://github.com/jayway/powermock/wiki/OldNews)

## Documentation
* [Getting Started](https://github.com/jayway/powermock/wiki/GettingStarted)
* [Downloads](https://github.com/jayway/powermock/wiki/Downloads)
* [Motivation](https://github.com/jayway/powermock/wiki/Motivation)
* Javadoc
  * [EasyMock API extension](http://www.javadoc.io/doc/org.powermock/powermock-api-easymock/1.6.4) ([PowerMock class](http://static.javadoc.io/org.powermock/powermock-api-easymock/1.6.4/org/powermock/api/easymock/PowerMock.html))
  * [Mockito API extension](http://www.javadoc.io/doc/org.powermock/powermock-api-mockito/1.6.4) ([PowerMockito class](http://static.javadoc.io/org.powermock/powermock-api-mockito/1.6.4/org/powermock/api/mockito/PowerMockito.html))
  * [PowerMock Reflect](http://www.javadoc.io/doc/org.powermock/powermock-reflect/1.6.4) ([Whitebox class](http://static.javadoc.io/org.powermock/powermock-reflect/1.6.4/org/powermock/reflect/Whitebox.html))
* [EasyMock](https://github.com/jayway/powermock/wiki/EasyMock)
  * [Mock Static](https://github.com/jayway/powermock/wiki/MockStatic)
  * [Mock Final](https://github.com/jayway/powermock/wiki/MockFinal)
  * [Mock Private](https://github.com/jayway/powermock/wiki/MockPrivate)
  * [Mock New](https://github.com/jayway/powermock/wiki/MockConstructor)
  * [Mock Partial](https://github.com/jayway/powermock/wiki/MockPartial)
  * [Replay and verify all](https://github.com/jayway/powermock/wiki/ReplayAllAndVerifyAll)
* Mockito
  * [Mockito 1.8+](MockitoUsage)
  * [Mockito 1.7](MockitoUsage_Legacy)
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
[![JAYWAY](http://www.jayway.com/wp-content/uploads/2014/05/jayway_rgb_inverted.png)](http://www.jayway.com/)

