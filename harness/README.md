# Harness

`harness/verify.sh --fast` checks the change against the base commit (`git config harness.base`); `.githooks/pre-commit` runs it on every commit and rejects on FAIL.
Static rules: no new `@Ignore`/`@Disabled`/`assumeTrue(false)`/`enabled = false`; no removed `@Test` methods or deleted test files; no removed assertion/expectation lines in test sources;
no `--add-opens`/`--add-exports` anywhere; no test-task configuration changes in Gradle files (`jvmArgs`, test `exclude`, filters, `useJUnit…`, `enabled`, `ignoreFailures`, …; dependency `exclude group:/module:` is allowed);
no edits under `harness/` or `.githooks/`; `core.hooksPath` must stay `.githooks`.
`harness/verify.sh` (no `--fast`) additionally runs the test ratchets: per module, the test count must not drop and failures must not rise versus
`harness/baseline_jdk8.tsv` (JDK 8, `clean test`) and `harness/baseline_jdk17.tsv` (JDK 17 with `harness/jdk17-opens.init.gradle`). `--jdk 8|17` runs one of them (JDK 8 ≈ 10 min, JDK 17 ≈ 3 min).
Output: `HARNESS<TAB>rule<TAB>PASS|FAIL<TAB>detail` lines, then `RESULT: PASS|FAIL`.
