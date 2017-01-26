/**
 * Regression: MethodNotFoundException
 * https://github.com/powermock/powermock/issues/717
 *
 * org.powermock.reflect.exceptions.MethodNotFoundException: No methods matching the name(s) accept were found in the class hierarchy of class java.lang.Object.
 at org.powermock.reflect.internal.WhiteboxImpl.getMethods(WhiteboxImpl.java:1720)
 at org.powermock.reflect.internal.WhiteboxImpl.getMethods(WhiteboxImpl.java:1745)
 at org.powermock.reflect.internal.WhiteboxImpl.getBestMethodCandidate(WhiteboxImpl.java:983)
 at org.powermock.core.MockGateway$MockInvocation.findMethodToInvoke(MockGateway.java:317)
 at org.powermock.core.MockGateway$MockInvocation.init(MockGateway.java:356)
 at org.powermock.core.MockGateway$MockInvocation.<init>(MockGateway.java:307)
 at org.powermock.core.MockGateway.doMethodCall(MockGateway.java:142)
 at org.powermock.core.MockGateway.methodCall(MockGateway.java:125)
 at InstanceFacadeImplTest.pendingInstanceStatusProcessorShouldDoNothing(InstanceFacadeI
 *
 */
package samples.powermockito.junit4.bugs.github717;