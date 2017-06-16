![PowerMock](powermock.png)

[![Build Status](https://travis-ci.org/powermock/powermock.svg?branch=master)](https://travis-ci.org/powermock/powermock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.powermock/powermock-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.powermock/powermock-core)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.powermock/powermock-core/badge.svg)](http://www.javadoc.io/doc/org.powermock/powermock-core)

Usage status: Mockito: [![Reference Status](https://www.versioneye.com/java/org.powermock:powermock-api-mockito/reference_badge.svg?style=flat)](https://www.versioneye.com/java/org.powermock:powermock-api-mockito/references) Easymock: [![Reference Status](https://www.versioneye.com/java/org.powermock:powermock-api-easymock/reference_badge.svg?style=flat)](https://www.versioneye.com/java/org.powermock:powermock-api-easymock/references) jUnit: [![Reference Status](https://www.versioneye.com/java/org.powermock:powermock-module-junit4/reference_badge.svg?style=flat)](https://www.versioneye.com/java/org.powermock:powermock-module-junit4/references) TestNG [![Reference Status](https://www.versioneye.com/java/org.powermock:powermock-module-testng/reference_badge.svg?style=flat)](https://www.versioneye.com/java/org.powermock:powermock-module-testng/references)

Writing unit tests can be hard and sometimes good design has to be sacrificed for the sole purpose of testability. Often testability corresponds to good design, but this is not always the case. For example final classes and methods cannot be used, private methods sometimes need to be protected or unnecessarily moved to a collaborator, static methods should be avoided completely and so on simply because of the limitations of existing frameworks.

PowerMock is a framework that extends other mock libraries such as EasyMock with more powerful capabilities. PowerMock uses a custom classloader and bytecode manipulation to enable mocking of static methods, constructors, final classes and methods, private methods, removal of static initializers and more. By using a custom classloader no changes need to be done to the IDE or continuous integration servers which simplifies adoption. Developers familiar with the supported mock frameworks will find PowerMock easy to use, since the entire expectation API is the same, both for static methods and constructors. PowerMock aims to extend the existing API's with a small number of methods and annotations to enable the extra features. Currently PowerMock supports EasyMock and Mockito.

When writing unit tests it is often useful to bypass encapsulation and therefore PowerMock includes several features that simplifies reflection specifically useful for testing. This allows easy access to internal state, but also simplifies partial and private mocking.

Please note that PowerMock is mainly intended for people with expert knowledge in unit testing. Putting it in the hands of junior developers may cause more harm than good.

## News
* 2017-06-16: PowerMock 1.7.0 has been released with support for Mockito 2 (not only beta versions) and new feature such as global `@PowerMockIgnore` As well as bug fixes and other improvements. See [release notes](https://github.com/powermock/powermock/releases/tag/powermock-1.7.0) and [change log](https://raw.githubusercontent.com/powermock/powermock/master/changelog.txt) for details. 
* 2017-02-03: Johan blogs about how to mock slf4j with PowerMock at his [blog](http://code.haleby.se/2017/02/03/a-case-for-powermock/)
* 2016-11-04: PowerMock 1.6.6 is released with bug fixes and other improvements. See [release notes](https://github.com/powermock/powermock/wiki/Release-Notes-PowerMock-1.6.6) and [change log](https://raw.githubusercontent.com/powermock/powermock/master/changelog.txt) for details.

[Older News](https://github.com/powermock/powermock/wiki/OldNews)

## Documentation
* [Getting Started](https://github.com/powermock/powermock/wiki/Getting-Started)
* [Downloads](https://github.com/powermock/powermock/wiki/Downloads)
* [Motivation](https://github.com/powermock/powermock/wiki/Motivation)
* Javadoc
  * [EasyMock API extension](http://www.javadoc.io/doc/org.powermock/powermock-api-easymock/1.7.0) ([PowerMock class](http://static.javadoc.io/org.powermock/powermock-api-easymock/1.7.0/org/powermock/api/easymock/PowerMock.html))
  * [Mockito API extension](http://www.javadoc.io/doc/org.powermock/powermock-api-mockito/1.7.0) ([PowerMockito class](http://static.javadoc.io/org.powermock/powermock-api-mockito/1.7.0/org/powermock/api/mockito/PowerMockito.html))
  * [Mockito2 API extension](http://www.javadoc.io/doc/org.powermock/powermock-api-mockito2/1.7.0) ([PowerMockito class](http://static.javadoc.io/org.powermock/powermock-api-mockito2/1.7.0/org/powermock/api/mockito/PowerMockito.html))
  * [PowerMock Reflect](http://www.javadoc.io/doc/org.powermock/powermock-reflect/1.7.0) ([Whitebox class](http://static.javadoc.io/org.powermock/powermock-reflect/1.7.0/org/powermock/reflect/Whitebox.html))
* Common
  * [PowerMock Configuration](https://github.com/powermock/powermock/wiki/PowerMock-Configuration)
  * [Bypass Encapsulation](https://github.com/powermock/powermock/wiki/Bypass-Encapsulation)
  * [Suppress Unwanted Behavior](https://github.com/powermock/powermock/wiki/Suppress-Unwanted-Behavior)
  * [Test Listeners](https://github.com/powermock/powermock/wiki/Test-Listeners)
  * [Mock Policies](https://github.com/powermock/powermock/wiki/Mock-Policies)
  * [Mock system classes](https://github.com/powermock/powermock/wiki/Mock-System)
* [EasyMock](https://github.com/powermock/powermock/wiki/EasyMock)
* [Mockito](https://github.com/powermock/powermock/wiki/Mockito)
* [TestNG](https://github.com/powermock/powermock/wiki/TestNG)  
* [Delegate to another JUnit Runner](https://github.com/powermock/powermock/wiki/JUnit_Delegating_Runner)
* [Bootstrap using a JUnit Rule](https://github.com/powermock/powermock/wiki/PowerMockRule)
* [Bootstrap using a Java Agent](https://github.com/powermock/powermock/wiki/PowerMockAgent)
* [OSGi](https://github.com/powermock/powermock/wiki/osgi)
* [Release Notes](https://github.com/powermock/powermock/wiki/ReleaseNotes)
* [FAQ](https://github.com/powermock/powermock/wiki/FAQ) 

## Support and discussion
Join the mailing-list [here](http://groups.google.com/group/powermock) for questions, feedback and support.

## Links
* [Change log](https://raw.githubusercontent.com/powermock/powermock/master/changelog.txt)
* [Mailing list for questions and support](http://groups.google.com/group/powermock)
