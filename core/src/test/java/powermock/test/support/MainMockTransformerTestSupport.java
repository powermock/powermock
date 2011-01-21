package powermock.test.support;

import org.powermock.core.classloader.MockClassLoader;

/**
 * This class is used when running tests in {@link org.powermock.core.transformers.impl.MainMockTransformerTest}. It is
 * placed in this package because classes in org.powermock.core.* are deferred by:
 * {@link MockClassLoader#packagesToBeDeferred}. Additionally, the class must be modified when it is loaded, and as such
 * not in {@link MockClassLoader#packagesToLoadButNotModify}.
 */
public class MainMockTransformerTestSupport {
    public static class SupportClasses {
        public final static class StaticFinalInnerClass {
        }

        public final static class FinalInnerClass {
        }

        public enum EnumClass {
            VALUE;
        }
    }
}
