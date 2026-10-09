#!/bin/bash
# harness/verify.sh [--fast] [--cached] [--jdk 8|17|all]
#   --fast    static rules only (no Gradle), < 5 s; used by .githooks/pre-commit
#   --cached  check the staged index instead of the working tree (pre-commit)
#   --jdk     which ratchet runs to do in full mode (default: all)
# Base commit: `git config harness.base` (set per worktree), else merge-base with book/lab-02-harness.
# Output: one machine-readable line per finding "HARNESS<TAB>rule<TAB>PASS|FAIL<TAB>detail", then "RESULT: PASS|FAIL".
set -u
cd "$(git rev-parse --show-toplevel)" || exit 2
FAST=0; CACHED=; JDKS="8 17"
while [ $# -gt 0 ]; do case $1 in
  --fast) FAST=1;; --cached) CACHED=--cached;; --jdk) shift; [ "$1" = all ] || JDKS=$1;;
  *) echo "usage: $0 [--fast] [--cached] [--jdk 8|17|all]"; exit 2;; esac; shift; done
BASE=$(git config harness.base || git merge-base HEAD book/lab-02-harness) || { echo "HARNESS	base	FAIL	no base commit"; exit 2; }
OUT=$(mktemp); trap 'rm -f $OUT' EXIT

git diff $CACHED -U0 --no-color --no-ext-diff --find-renames "$BASE" > "$OUT.diff"
git diff $CACHED --name-status --no-renames "$BASE" > "$OUT.status"
python3 - "$OUT.diff" "$OUT.status" <<'PY' | tee "$OUT"
import re, sys
diff, status = open(sys.argv[1], errors="replace").read(), open(sys.argv[2]).read()
fails = []
def fail(rule, detail): fails.append((rule, detail))
# 1) harness is off-limits
for line in status.splitlines():
    st, path = line.split("\t", 1)
    if path.startswith(("harness/", ".githooks/")): fail("harness-tamper", f"{st} {path}")
    if st == "D" and "/src/test/" in path: fail("test-file-deleted", path)
# 2) per-hunk line rules
DISABLE = re.compile(r"@Ignore\b|@Disabled\b|assumeTrue\s*\(\s*false\s*\)|assumeFalse\s*\(\s*true\s*\)|enabled\s*=\s*false")
OPENS = re.compile(r"--add-opens|--add-exports|--illegal-access|Add-Opens|Add-Exports|implAddOpens|addOpens\s*\(")
TESTCFG = re.compile(r"jvmArgs|jvmArgumentProviders|\bexclude\b|excludeTestsMatching|includeTestsMatching|\bfilter\s*\{|useJUnit|useTestNG|useJUnitPlatform|\benabled\b|ignoreFailures|onlyIf|failFast|forkEvery|systemPropert")
ALLOW = re.compile(r"exclude\s*\(?\s*group\s*:|exclude\s*\(?\s*module\s*:")  # dependency excludes are fine
ASSERT = re.compile(r"\bassert\w*\s*\(|\bexpect\w*\s*\(|\bverify\w*\s*\(|\bfail\s*\(|@Test\b|expected\s*=")
f, tests = None, {}
for l in diff.splitlines():
    if l.startswith("+++ "): f = l[6:] if l.startswith("+++ b/") else f; continue
    if l.startswith("--- a/"): f = l[6:]; continue
    if not f or f.startswith(("harness/", ".githooks/")) or l.startswith(("+++", "---")) or l[:1] not in "+-": continue
    sign, text = l[0], l[1:]
    build = f.endswith((".gradle", ".gradle.kts", "gradle.properties")) or f.endswith("MANIFEST.MF")
    if sign == "+" and DISABLE.search(text): fail("test-disabled", f"{f}: {text.strip()}")
    if sign == "+" and OPENS.search(text): fail("jvm-opens-added", f"{f}: {text.strip()}")
    if build and TESTCFG.search(text) and not ALLOW.search(text): fail("test-config-changed", f"{f}: {sign}{text.strip()}")
    if "/src/test/" in f and f.endswith(".java"):
        if text.count("@Test"): tests[f] = tests.get(f, 0) + (text.count("@Test") if sign == "+" else -text.count("@Test"))
        if sign == "-" and ASSERT.search(text): fail("test-expectation-changed", f"{f}: -{text.strip()}")
for f, n in tests.items():
    if n < 0: fail("test-method-removed", f"{f}: {-n} @Test fewer")
rules = ["harness-tamper", "test-file-deleted", "test-disabled", "jvm-opens-added", "test-config-changed", "test-expectation-changed", "test-method-removed"]
for r in rules:
    hits = [d for rr, d in fails if rr == r]
    print(f"HARNESS\t{r}\t{'FAIL' if hits else 'PASS'}\t{len(hits)}")
    for d in hits[:20]: print(f"  -> {r}: {d}")
PY
rm -f "$OUT.diff" "$OUT.status"
[ "$(git config core.hooksPath)" = .githooks ] && echo "HARNESS	hooks-path	PASS	.githooks" | tee -a "$OUT" || echo "HARNESS	hooks-path	FAIL	core.hooksPath=$(git config core.hooksPath)" | tee -a "$OUT"

if [ $FAST = 0 ]; then
  SP=/private/tmp/claude-502/-Users-rat-git-buecher-schreiben/f7c0d745-1f59-4c84-b89b-c20bc418fc4b/scratchpad/spike
  export GRADLE_USER_HOME=${GRADLE_USER_HOME:-$SP/gradle-home}
  for v in $JDKS; do
    T0=$(date +%s); LOG=harness/.last_jdk$v.log
    args="clean test --continue"; [ $v = 17 ] && args="$args --init-script harness/jdk17-opens.init.gradle"
    [ $v = 8 ] || rm -rf tests/java8/*/build  # java8 modules are disabled on newer JDKs; stale XML would count
    JAVA_HOME=$SP/jdks/$v/Contents/Home ./gradlew --no-daemon $args > $LOG 2>&1
    python3 - . $T0 harness/baseline_jdk$v.tsv $v <<'PY' | tee -a "$OUT"
import sys, glob, os, xml.etree.ElementTree as ET
from collections import defaultdict
root, since, base, v = sys.argv[1], float(sys.argv[2]), sys.argv[3], sys.argv[4]
now = defaultdict(lambda: [0, 0])  # tests, fail+error
for f in glob.glob(f"{root}/**/build/test-results/**/*.xml", recursive=True):
    if os.path.getmtime(f) < since or "/harness/" in f: continue
    s = ET.parse(f).getroot(); m = os.path.relpath(f, root).split("/build/")[0]
    now[m][0] += int(s.get("tests", 0)); now[m][1] += int(s.get("failures", 0)) + int(s.get("errors", 0))
bad = 0
for line in open(base).read().splitlines()[1:]:
    m, t, p, fl, er, sk = line.split("|")
    if m == "TOTAL": continue
    t, fe = int(t), int(fl) + int(er)
    nt, nfe = now.get(m, [0, 0])
    if nt < t: bad += 1; print(f"  -> ratchet-jdk{v}: {m} tests {nt} < baseline {t}")
    if nfe > fe: bad += 1; print(f"  -> ratchet-jdk{v}: {m} failures {nfe} > baseline {fe}")
tt = sum(x[0] for x in now.values()); tf = sum(x[1] for x in now.values())
print(f"HARNESS\tratchet-jdk{v}\t{'FAIL' if bad else 'PASS'}\t{bad} regressions; now tests={tt} fail+error={tf}")
PY
  done
fi
if /usr/bin/grep -q "	FAIL	" "$OUT"; then echo "RESULT: FAIL (see lines marked FAIL above; rules in harness/README.md)"; exit 1; fi
echo "RESULT: PASS"
