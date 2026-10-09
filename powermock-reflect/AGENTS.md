# powermock-reflect: agent compass

## Purpose
- Reflection helpers for private/internal state (`Whitebox`: get/set fields, invoke private methods and constructors). Runtime deps: objenesis, byte-buddy.
- It sits at the bottom of the dependency graph. `powermock-core`, `powermock-api-support`, `powermock-classloading-base`, `powermock-module-junit4-common` and `powermock-module-testng-common` pull it in via `api(...)` (`gradle/modules.gradle`). Any change here affects the whole repo.

## Build / test
- There is no real module build file: `powermock-reflect/build.gradle` is empty. Dependencies are in `gradle/modules.gradle`; compile and test settings are in `gradle/java-module.gradle`.
- `JAVA_HOME=<jdk8> ./gradlew :powermock-reflect:test` gives 120 tests: 118 pass, 2 `@Ignore`d.
- Checkstyle is only applied when Gradle runs on JDK 8: `./gradlew :powermock-reflect:checkstyleMain`.
- JDK 17 fails 36 tests unless the test JVM gets `java.lang` opened. Nothing in the build adds `--add-opens`. This makes it green again (118/0/2): `JAVA_TOOL_OPTIONS=--add-opens=java.base/java.lang=ALL-UNNAMED JAVA_HOME=<jdk17> ./gradlew :powermock-reflect:test`

## Key files
- `Whitebox.java`: public facade. Almost every method is a one-line delegation to `WhiteboxImpl`.
- `internal/WhiteboxImpl.java` (~2,700 lines): the real logic for member lookup, invocation and field setting.
- `internal/proxy/ProxyFrameworks.java`: unwraps JDK proxies and CGLIB proxies (the CGLIB check looks for a `CGLIB$SET_THREAD_CALLBACKS` method).
- `internal/ConstructorFinder.java` and `CandidateConstructorSearcher.java`: resolve constructors from argument types.
- `matching/FieldMatchingStrategy.java` and `internal/matcherstrategies/*`: rules for picking fields by name, type or annotation.

## Gotchas
- `WhiteboxImpl.doGetAllMethods` walks up to `Object` and calls `setAccessible(true)` on every declared method. It skips only `finalize`. On JDK 9+ this breaks on `Object.clone()`, which caused 25 of the 36 JDK 17 failures.
- `allClassMethodsCache` is a static `ConcurrentHashMap<Class, Method[]>` that is never cleared. Results are cached per `Class` for the life of the JVM.
- Setting a `final` field goes through `sun.misc.Unsafe` (`setStaticFieldUsingUnsafe` / `setFieldUsingUnsafe`), not `Field.set`. A test asserts that the field's modifiers are unchanged afterwards.
- On JDK 17, tests that build proxies with `cglib-nodep` (test scope) fail with `NoClassDefFoundError: net.sf.cglib.proxy.Enhancer` until `java.lang` is opened.
- Gradle's `test` excludes `**/*Defect*` and `**/*TestCase*`. A test class named that way never runs.

## Be careful with
- The public signatures of `Whitebox` and `WhiteboxImpl`: core and the mock APIs call `WhiteboxImpl` directly.
- Exception types in `exceptions/*` (for example `TooManyMethodsFoundException` or `MethodNotFoundException`): tests assert on them, and so do their messages.
- `sourceCompatibility`/`targetCompatibility` = 1.8: keep the code Java 8 compatible.

## Verify a change
1. `JAVA_HOME=<jdk8> ./gradlew :powermock-reflect:test :powermock-reflect:checkstyleMain`: expect 118 pass, 2 skipped.
2. Run the same test with JDK 17 plus the `JAVA_TOOL_OPTIONS` above: expect 118 pass, 0 fail.
3. Then run `./gradlew :powermock-core:test`, because core is the first consumer.
