# powermock-api: compass (mockito2 + support)

## Purpose
- `powermock-api-support`: framework-neutral API helpers (`MemberMatcher`/`MemberModifier`, `Stubber`, `SuppressCode`, `MethodProxy`). Used by `powermock-api-easymock` (out of scope), `powermock-api-mockito2` and `powermock-classloading-base`.
- `powermock-api-mockito2`: the `PowerMockito` API plus `PowerMockMaker`, a Mockito `MockMaker` that delegates to a configurable real MockMaker. Consumed by all `tests:mockito:*`, `tests:java8:mockito-*`, `tests:java11:*` and the `powermock-release:*mockito2*` bundles.
- No per-module `build.gradle`: dependencies live in `gradle/modules.gradle`; versions in root `build.gradle` `ext` (`mockitoVersion`, `byteBuddy`; both depend on the build JVM, see root `AGENTS.md`). Source/target is 1.8 (`gradle/java-module.gradle`).

## Build / test
- `JAVA_HOME=<jdk8> ./gradlew :powermock-api:powermock-api-mockito2:test` (6 tests, all green on JDK 8).
- `:powermock-api:powermock-api-support:test` is `NO-SOURCE`: support has no unit tests of its own.
- Checkstyle runs only when Gradle runs on JDK 8 (`java-module.gradle`), so build on JDK 8 before committing.

## Key files
- `powermock-api-mockito2/.../mockmaker/PowerMockMaker.java`: registered MockMaker; `getHandler` returns a dummy `StaticMockHandler` for `Class` mocks and unwraps PowerMock instance mocks via `MockRepository`.
- `.../mockmaker/MockMakerLoader.java`: picks the delegate from `mockito.mock-maker-class`: unset = Mockito default, `mock-maker-inline` = inline maker, otherwise a class name loaded via the context ClassLoader.
- `src/main/resources/mockito-extensions/org.mockito.plugins.MockMaker` and `...StackTraceCleanerProvider`: Mockito plugin hooks that activate PowerMock; the class names in them must match the Java classes.
- `.../internal/mockcreation/DefaultMockCreator.java`: creates mocks and registers `Mockito.reset()` as an after-method runner.
- `powermock-api-mockito2/src/test/.../PowerMockMockito2ApiTestSuite.java`: the only way the `*TestCase` classes run by default (see gotchas).

## Gotchas
- The module is named "mockito2" but builds against Mockito 5.24.0 when Gradle runs on JDK 11+ and Mockito 4.3.1 on JDK 8 (`build.gradle` `ext`); published artifacts follow the build JVM. Mockito's own byte-buddy is excluded everywhere (`exclude group: 'net.bytebuddy'`), so the Byte Buddy version comes from `powermock-core`. Keep that exclude on any new Mockito dependency.
- `java-module.gradle` excludes `**/*TestCase*` and `**/*Defect*` from `test`. New unit tests named `*TestCase` only run if you add them to `PowerMockMockito2ApiTestSuite`, which also fixes their order (the suite comment says the tests are flaky when reordered).
- Users configure the delegate in `org/powermock/extensions/configuration.properties` on the test classpath (see `tests/mockito/inline/src/test/resources/...`: `mockito.mock-maker-class=mock-maker-inline`). `PowerMockMakerTestCase` copies `configuration.template` to that file in `build/resources/test` and deletes it afterwards. If a run aborts mid-test and leaves the file behind, the next run fails with "Test data not created". Delete it or run `clean`.
- `PowerMockMakerTestCase` builds its isolated `URLClassLoader` from `java.class.path` (the app ClassLoader is no `URLClassLoader` since JDK 9), so it relies on that property holding the full test classpath (true for Gradle's test worker, not for a manifest-only/argfile launcher). Its `assumeFalse` still skips `java.version` 9*; on JDK 11 the module is 6/6 green.
- Three classes import `org.mockito.internal.*` (`PowerMockitoInjectingAnnotationEngine`, `PowerMockitoSpyAnnotationEngine`, `api/extension/listener/AnnotationEnabler`). They are the first to break when you bump `mockitoVersion`.

## Be careful with
- Package/class names of `PowerMockMaker` and `StackTraceCleanerProvider`: the plugin files and users' configs reference them by string.
- `MockMakerLoader` string keys (`mock-maker-inline`): user configuration depends on them.

## Verify a change
- Unit: the mockito2 `test` task above, on JDK 8 and 11.
- Integration (the main coverage): `:tests:mockito:junit4:test`, `:tests:mockito:inline:test` (the delegate path; on JDK 8: 6 tests, 4 pass, 2 skipped), `:tests:mockito:junit4-agent:test`, `:tests:mockito:junit4-rule-xstream:test`, `:tests:mockito:testng:test`. The agent, rule and testng variants put the `tests:mockito:junit4` test classes on their classpath.
