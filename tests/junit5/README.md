# tests/junit5 — acceptance spec for `PowerMockExtension` (JUnit Jupiter 5.14.4)

All classes: `@ExtendWith(PowerMockExtension.class)`, package `powermock.modules.junit5.acceptance`, class-level annotations only.
Red column = result on the empty skeleton extension (same on JDK 17+opens and JDK 8: 18 tests, 4 pass, 14 fail).

| # | Class | Sample class(es) | Key assertion | Red on skeleton |
|---|-------|------------------|---------------|-----------------|
| T1 | T1StaticMockingTest | singleton.StaticService | `say`/`sayFinal` return the stub; `verifyStatic` | ClassNotPreparedException (2/2) |
| T2 | T2FinalClassMockingTest | finalmocking.FinalDemo | `mock(FinalDemo)` stubs `say` | Mockito "cannot mock final class" (1/1) |
| T3 | T3FinalMethodMockingTest | simplemix.SimpleMix, privateandfinal.PrivateFinal | final `calculate()` of a mock returns 42; private final `sayIt` of a spy stubbed | real final body runs: NPE / InvalidUseOfMatchers (2/2) |
| T4 | T4ConstructorInterceptionTest | expectnew.ExpectNewDemo, newmocking.MyClass | `new MyClass()` inside ExpectNewDemo returns the `whenNew` mock / throws; `verifyNew` | `new` not intercepted (no exception thrown; whenNew state leaks between tests) (2/2) |
| T5 | T5WhiteboxOnPreparedClassTest | simplemix.SimpleMix(+Collaborator, final), staticinitializer.StaticInitializerExample | final-class mock injected with `setInternalState`, read back with `getInternalState`; static final field set on a class whose static init is suppressed (`@SuppressStaticInitializationFor`) | "cannot mock final class"; ExceptionInInitializerError (2/2) |
| T6 | T6ChunkATest / T6ChunkBTest | StaticService (A) / staticandinstance.StaticAndInstanceDemo (B) | own prepared class mockable; the class prepared only in the other chunk returns its real value | ClassNotPreparedException (1/2 each; "real" tests pass) |
| T7 | T7NoPrepareForTestTest | StaticService, MyClass, Service | real code + plain Mockito mocks work | green by design (empty extension changes nothing) |
| T8 | T8LifecycleTest | StaticService | `mockStatic` in @BeforeEach seen by test, `verifyStatic` in @AfterEach; static field written in @BeforeAll visible to the test | ClassNotPreparedException in @BeforeEach (3/3) |

Implementers: @BeforeAll/@BeforeEach/@AfterEach must run against the MockClassLoader copy of the test class
(T8 reads a static field written by @BeforeAll). PowerMock state (MockRepository) must be cleared after each test
(T4). Run: `./gradlew :tests:junit5:test` (JDK 9+ needs the lab opens init script).
