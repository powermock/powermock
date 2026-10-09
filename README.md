![PowerMock](powermock.png)

[![Build Status](https://travis-ci.org/powermock/powermock.svg?branch=master)](https://travis-ci.org/powermock/powermock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.powermock/powermock-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.powermock/powermock-core)
[ ![Download](https://api.bintray.com/packages/powermock/maven/powermock/images/download.svg) ](https://bintray.com/powermock/maven/powermock/_latestVersion)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.powermock/powermock-core/badge.svg)](http://www.javadoc.io/doc/org.powermock/powermock-core)

## Revival (2026)

This is a fork of [powermock/powermock](https://github.com/powermock/powermock), revived as the hands-on project of a book on agent-assisted software development. It is **not** upstream and does not publish releases. The branch `book/consolidated` holds the consolidated state of the book's labs 0–8 (PR #10 against `release/2.x`); `revival/jdk17-21-green` makes all four CI cells green (EasyMock 5, opens in the build).

### What works

Local verification of `revival/jdk17-21-green` (2026-09-27) (`./gradlew clean test --continue`, serial, no init script). On Java 9+ the build itself passes `--add-opens java.base/java.lang=ALL-UNNAMED` and `--add-opens java.base/java.util=ALL-UNNAMED` to the test JVMs (see `build.gradle`); Java 8 runs unchanged.

| JDK | Tests | Pass | Fail | Skip |
|---|---|---|---|---|
| 8 | 1654 | 1524 | 0 | 130 |
| 11 | 1645 | 1516 | 0 | 129 |
| 17 | 1645 | 1520 | 0 | 125 |
| 21 | 1645 | 1514 | 0 | 131 |

On every JDK: `tests/mockito/*` 668 tests, 0 failures; `tests/junit5` 18/18; all `tests/easymock/*` modules green (EasyMock 5.7.0).

CI (`.github/workflows/build.yml`) runs JDK 8, 11, 17 and 21; all four cells are blocking.

Mockito version depends on the JVM that runs Gradle: JDK 8 builds against Mockito 4.3.1 (Byte Buddy 1.14.11), JDK 11+ against Mockito 5.24.0 (Byte Buddy 1.17.7). Published artifacts follow the build JVM. The recommended release build is JDK 11+; Java 8 users then have to pin Mockito 4.x themselves, because Mockito 5 requires Java 11. EasyMock 5.7.0 brings Byte Buddy 1.18.12 and Objenesis 3.6, which win dependency resolution in every module that has EasyMock on the classpath (on JDK 8 too).

### Known limitations

- **PowerMock on Java 17+ needs `--add-opens java.base/java.lang=ALL-UNNAMED`** in the user's test JVM (javassist/cglib `ClassLoader.defineClass`, EasyMock replicas of final system classes). `java.base/java.util` is only needed by this repository's own tests (system-rules `EnvironmentVariables`). Without opens, JDK 17/21 are not supported.
- **EasyMock API: sealed system classes are mocked through a replica.** JDK 21 seals e.g. `java.net.InetAddress` and `java.io.Console`; a subclass mock is impossible, so PowerMock uses the same replica path it uses for final `java.*` classes: the mock is an Objenesis instance of the real type, stubbing works only in classes prepared with `@PrepareForTest`, and it must be replayed/verified through `PowerMock`, not plain `EasyMock`. On JDK 17 and older these classes are still subclass mocks. Sealed classes outside `java.*` and the Mockito API are not covered.
- **EasyMock 5 message format:** `Unexpected method call` messages now read `EasyMock for class X -> X.m(..)` and no longer end with `:`. PowerMock's `expectNew` messages keep the old form (the internal substitute mock is stripped).
- `PowerMock.isEasyMocked` (nice replay/verify mode) still checks for cglib proxies and does not recognise EasyMock class mocks; this was already the case with EasyMock 4.x (repackaged cglib).
- **`powermock-module-junit5` (JUnit Jupiter) is experimental.** It passes the 18 acceptance tests in `tests/junit5`, nothing more. Not supported: `@Mock`/`@InjectMocks` field injection, constructor/parameter injection, `@Nested` classes. Interaction with other Jupiter extensions is untested.
- Requirement tests that need a SecurityManager are skipped on JDK 18+ (JEP 411); tests that expect closed JDK internals are skipped where `java.util.concurrent` is open (JDK 8, JDK 9–15 default).

### Known failing tests

None in the local matrix or in CI on JDK 8, 11, 17 and 21. `StringConstructorWorksWhenExtendingTestCase` (EasyMock, JUnit 3 style) is not executed by the Gradle test task and still asserts the EasyMock 4 message text.

### Lab branches

The `book/lab-*` branches are the book's exercise material: each lab's runs, the chosen best result (`book/lab-NN-best*`) and the inputs. They are kept for reference and **must not be merged**. Some contain deliberate defects and prompt-injection text used in the security and verification labs (Lab 5 `book/lab-05-pr-2` to `book/lab-05-pr-4`, Lab 7 `book/lab-07-I1-*` and `book/lab-07-I2-*`).

## About PowerMock

Writing unit tests can be hard and sometimes good design has to be sacrificed for the sole purpose of testability. Often testability corresponds to good design, but this is not always the case. For example final classes and methods cannot be used, private methods sometimes need to be protected or unnecessarily moved to a collaborator, static methods should be avoided completely and so on simply because of the limitations of existing frameworks.

PowerMock is a framework that extends other mock libraries such as EasyMock with more powerful capabilities. PowerMock uses a custom classloader and bytecode manipulation to enable mocking of static methods, constructors, final classes and methods, private methods, removal of static initializers and more. By using a custom classloader no changes need to be done to the IDE or continuous integration servers which simplifies adoption. Developers familiar with the supported mock frameworks will find PowerMock easy to use, since the entire expectation API is the same, both for static methods and constructors. PowerMock aims to extend the existing API's with a small number of methods and annotations to enable the extra features. Currently PowerMock supports EasyMock and Mockito.

When writing unit tests it is often useful to bypass encapsulation and therefore PowerMock includes several features that simplifies reflection specifically useful for testing. This allows easy access to internal state, but also simplifies partial and private mocking.

Please note that PowerMock is mainly intended for people with expert knowledge in unit testing. Putting it in the hands of junior developers may cause more harm than good.

## News
* 2019-04-21: PowerMock 2.0.2 has been released and is avaliable in Maven Central. The release includes fixes for [issue](https://github.com/powermock/powermock/issues/979) with PowerMock JavaAgent and the latest JDK and a [security issue](https://github.com/powermock/powermock/issues/973) with the build script. 
* 2019-01-07: PowerMock 2.0.0 has been released. Main changes: offical supporting Mockito 2.x and dropping supporting Mockito 1.x. This release also supports Java 9. Other change read in [release notes](https://github.com/powermock/powermock/releases/tag/powermock-2.0.0). 
* 2017-08-12: PowerMock 1.7.1 has been released with one, but significant change: the old API for verifying static mock has been deprecated and a new one has been added. Old API will be removed in version PowerMock 2.0 due to incompatibility with Mockito Public API.
* 2017-06-16: PowerMock 1.7.0 has been released with support for Mockito 2 (not only beta versions) and new features such as global `@PowerMockIgnore` as well as bug fixes and other improvements. See [release notes](https://github.com/powermock/powermock/releases/tag/powermock-1.7.0) and [change log](https://raw.githubusercontent.com/powermock/powermock/master/docs/changelog.txt) for details. 
* 2017-02-03: Johan blogs about how to mock slf4j with PowerMock at his [blog](http://code.haleby.se/2017/02/03/a-case-for-powermock/)

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

## Contributing to PowerMock

Please, read [the guideline](CONTRIBUTING.md) for a new contributor before start. 

### Support and discussion
Join the mailing-list [here](http://groups.google.com/group/powermock) for questions, feedback and support.

