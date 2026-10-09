# powermock-core — agent compass

## Purpose
- Engine of PowerMock: `MockClassLoader` + Javassist transformer chain that rewrites classes (final/static/native/constructors/static init), plus `GlobalConfiguration` and the test-annotation extractors (`org.powermock.tests.utils`).
- Depends on `powermock-reflect` (Whitebox), javassist, byte-buddy (`gradle/modules.gradle`). Every `powermock-api-*` and `powermock-module-*` project pulls it in via `api(project(":powermock-core"))`.

## Build / test
- `JAVA_HOME=<jdk8> ./gradlew :powermock-core:test --continue` → baseline 185 tests, 150 pass, 35 skip, 0 fail.
- Skips are expected: transformer tests are parameterized over `TransformStrategy` (CLASSLOADER / INST_REDEFINE) and use `Assume` to drop unsupported combos.
- Checkstyle is applied only when Gradle runs on JDK 8: `./gradlew :powermock-core:checkstyleMain :powermock-core:checkstyleTest`.
- Test task excludes `**/*Defect*` and `**/*TestCase*` — a class named like that never runs.
- JDK 17 without opens: 48 failures (`InaccessibleObjectException`). With `JAVA_TOOL_OPTIONS="--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED"` the 48 access failures disappear; the one `ClassCastException` that remained (`PowerMockIgnorePackagesExtractorImplTest`) was a test-factory bug fixed for JDK 11+.

## Key files (under `src/main/java/org/powermock/`)
- `core/classloader/MockClassLoaderConfiguration.java` — decides defer vs. load-unmodified vs. transform (`PACKAGES_TO_BE_DEFERRED`, `PACKAGES_TO_LOAD_BUT_NOT_MODIFY`).
- `core/classloader/DeferSupportingClassLoader.java` — child-first unless deferred, per-class locks, SoftReference cache; resources delegated to `deferTo`.
- `core/classloader/javassist/JavassistMockClassLoader.java` — the only `MockClassLoader` impl; even unmocked classes are re-defined from the `ClassPool`.
- `core/transformers/javassist/JavassistMockTransformerChainFactory.java` — fixed transformer order; append new transformers here.
- `configuration/GlobalConfiguration.java` + `configuration/support/ConfigurationFactoryImpl.java` — per-thread config; precedence env vars > `org/powermock/extensions/configuration.properties` > `org/powermock/default.properties`.

## Gotchas
- `ByteCodeFramework` has a single constant `Javassist`; the `core/bytebuddy` package is helpers only, not a second classloader.
- Classes not deferred are defined again by the MockClassLoader → different `Class` identity than the system loader (ClassCastException across loaders).
- Anything under `org.powermock.*` is loaded but never modified, except `org.powermock.example*`; `org.powermock.core*` is always deferred (also `powermock.global-ignore` default).
- `findResource(s)` and many tests go through `Whitebox` reflection into JDK internals → breaks on JDK 16+ without `--add-opens`.
- `GlobalConfiguration` holds a static factory and ThreadLocals: tests that call `setConfigurationFactory` must call `GlobalConfiguration.clear()` afterwards.
- `ConfigurationFactoryImplTest` mutates env vars via system-rules (needs `java.util` opened on 17).

## Be careful with
- Defer/ignore package lists and transformer order: every downstream module and `tests/*` project relies on them; changes show up far from core.
- Keep sources Java 8 compatible (`sourceCompatibility = 1.8`).

## Verify a change
- `:powermock-core:test` on JDK 8 must stay 150/0/35; re-run on JDK 17 with the opens above.
- For classloader/transformer changes also run a consumer suite, e.g. `:tests:mockito:junit4:test` (JDK 8: 232 tests, 0 fail).
