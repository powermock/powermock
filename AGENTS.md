# PowerMock: root / build compass
PowerMock 2.0.10 (`version.properties`) extends EasyMock/Mockito via bytecode manipulation (static/final/private mocking). Java 8 baseline, Gradle 8.14.3 wrapper.
- Modules (`settings.gradle`): `powermock-reflect` → `powermock-core` → `powermock-api:{support,easymock,mockito2}`; `powermock-classloading:{base,objenesis,xstream}`; `powermock-modules:{javaagent,junit4*,testng*}`; `tests:*` (test-only); `powermock-release:*` (shadow "full" jars).
- Area compasses: `powermock-reflect/AGENTS.md`, `powermock-core/AGENTS.md`, `powermock-api/AGENTS.md`, `tests/AGENTS.md`.

## Commands
- Full suite: `JAVA_HOME=/path/to/jdk ./gradlew clean test --continue` (CI runs `./gradlew test --continue`).
- One module: `./gradlew :powermock-reflect:test`; list modules: `./gradlew projects`.
- Switch JDK = set `JAVA_HOME`; no toolchains are configured, so Gradle itself runs on that JDK and it also runs the tests.

## Key build files
- `build.gradle`: all dependency versions (`ext {}` block), buildscript plugins (shadow 8.1.1, animalsniffer).
- `gradle/modules.gradle`: per-module dependencies for all library modules; defines publishable vs. unpublishable sets.
- `gradle/java-module.gradle`: `provided` config, source/target 1.8, checkstyle, test excludes. Applied to library modules and `tests/*`.
- `tests/build.gradle`, `tests/{easymock,mockito,java8,java11}/build.gradle`: test-module wiring, `-javaagent` jvmArgs, TestNG suites.
- `.github/workflows/build.yml`: matrix JDK 8/11/17/21 (temurin).

## Gotchas
- `provided` is hand-rolled (propdeps plugin repo is dead): plain configuration added to main compile + test compile/runtime classpaths; `publish-maven.gradle` writes it into the POM as `scope provided`. Only user: `powermock-module-junit4-rule` → `classloading-base`.
- Tests named `*Defect*` or `*TestCase*` are excluded from `test` (`java-module.gradle`). Such a class never runs.
- `tests/java8/*`: all tasks (incl. `clean`) disabled on JDK != 8, so JDK 11 counts 9 fewer tests, and stale JDK-8 XML results survive `clean`. Count only fresh result files.
- Checkstyle is applied only when Gradle runs on JDK 8.
- `tests:testng` needs its `useTestNG { suites 'suite.xml' }` block. Without it the module runs 0 tests and the build still passes.
- CI: JDK 8 and 11 must pass; 17/21 are `experimental` (`continue-on-error`), known red until module-access (`--add-opens`) work.
- `tests/java11` has JDK-11 gating commented out (TODO); it runs on every JDK.

## Be careful with
- Mockito/Byte Buddy versions depend on the JDK Gradle runs on (`build.gradle ext`): Java 11+ = Mockito 5.24.0 + Byte Buddy 1.17.7, JDK 8 = Mockito 4.3.1 + Byte Buddy 1.14.11. PowerMock main code must compile against both (e.g. `PowerMockMaker.createMock` uses the raw erased signature).
- Don't bump versions in `build.gradle ext` casually (mockito excludes byte-buddy to pin `byteBuddy`). Don't touch the wrapper SHA-256 pin or `gradlew*` by hand.
- `-PcheckJava6Compatibility` applies animalsniffer (signature `java18`) to publishable modules; changing `sourceCompatibility` breaks the 1.8 contract of the published jars.

## Verify a change
- Golden state (`clean test --continue`): JDK 8 = 1599 tests / 1474 pass / 0 fail / 125 skip; JDK 11 = 1590 / 1465 / 0 / 125 (2 failures before the two JDK-11 test fixes). JDK 17 without `--add-opens` ~750 failures (756 measured before the JDK 11 test fixes): known, not a regression.
- Sum `build/test-results/test/*.xml` per module and diff against the previous run. `BUILD SUCCESSFUL` alone proves nothing (a module can silently drop to 0 tests).
