# tests/ — integration-test modules (compass)

## Purpose
- Not published; each module runs PowerMock's `samples.*` against one combo of mock API x runner: `easymock/*` (JUnit 4.3–4.12 pinned per module, TestNG, javaagent), `mockito/*` (JUnit4, inline, rules w/ objenesis/xstream, delegate, agent, TestNG), `junit4`, `testng`, `java8/*`, `java11/*`. `utils` = shared sample classes.

## Commands
- One module: `JAVA_HOME=/path/to/jdk8 ./gradlew :tests:mockito:junit4:test` (paths = `settings.gradle` includes, e.g. `:tests:easymock:testng:test`).
- Everything, like CI (`.github/workflows/build.yml`, JDK 8/11 blocking, 17/21 non-blocking): `./gradlew clean test --continue`.
- Switch JDK only via `JAVA_HOME`; sources/targets stay 1.8 (`gradle/java-module.gradle`). Checkstyle is applied only on JDK 8.

## Key files
- `tests/build.gradle` — applies `gradle/java-module.gradle` to all subprojects; defines `utils`, `junit4`, `testng`.
- `tests/{easymock,mockito,java8,java11}/build.gradle` — per-module deps, JUnit version pins, test filters, agent `jvmArgs`.
- `tests/**/suite.xml` — TestNG suites (`testng`, `easymock/testng`, `easymock/testng-agent`, `mockito/testng`).
- `gradle/java-module.gradle` — global test exclude `**/*Defect*`, `**/*TestCase*`.

## Gotchas
- `tests/java8/*`: `if (JavaVersion.current() != VERSION_1_8) project.tasks.all { enabled = false }` — on any other JDK *every* task is SKIPPED, including `clean`. Old JDK 8 XML stays in `tests/java8/*/build/test-results`; don't count those as a fresh run. Delete `build/` by hand if needed.
- `tests/java11/*`: the matching JDK-11 guard is commented out (TODO) — it runs on every JDK, including 8.
- TestNG modules run ONLY classes listed in their `suite.xml`, wired via `test { useTestNG() { suites 'suite.xml' } }`. Lose that block and Gradle falls back to JUnit: 0 tests, BUILD SUCCESSFUL (verified on `:tests:testng`). A new TestNG class not added to `suite.xml` never runs.
- Shared test output: `mockito:junit4` test classes feed `mockito:{junit4-agent,junit4-rule-*,junit4-delegate,testng}`, `java8/*`, `java11/*`; `easymock:junit4` feeds `easymock:junit45..412`. One edit there changes counts in many modules.
- Filters differ per module: `mockito:junit4`/`inline` exclude `**/*Cases*`; `easymock:junit4`/`junit45` exclude `**/*Defect*`; `mockito:junit4-delegate` has `scanForTestClasses = false` and only includes `*Test`/`*Tests` classes.
- `*-agent` modules add `-javaagent:` pointing at the `powermock-module-javaagent` jar.

## Don't touch lightly
- `useTestNG`/`suites` blocks, JUnit version pins (they are the point of `easymock:junit4x`), the `java8` guard, the `exclude` patterns.

## Verify
- Count tests per module from `build/test-results/test/*.xml`; BUILD SUCCESSFUL proves nothing.
- Baseline JDK 8 `clean test --continue`: 1599 tests = 1474 pass / 0 fail / 125 skip (repo-wide). Largest: `easymock:junit4` 304 (5 skip), `mockito:junit4` 232, `mockito:junit4-agent` 129, `mockito:junit4-delegate` 112, `mockito:junit4-rule-objenesis` 93 (54 skip), `mockito:junit4-rule-xstream` 94. TestNG: `easymock:testng` 24, `easymock:testng-agent` 22, `testng` 1, `mockito:testng` 1.
- JDK 11: 9 fewer tests (the 4 `java8` modules: 6+1+1+1 are SKIPPED); otherwise same per-module numbers.
