package org.powermock.core.transformers.support;

import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.MockTransformerChain.FilterPredicate;

public class FilterPredicates {
    
    public static FilterPredicate isInstanceOf(final Class<?> klass) {
        return new FilterPredicate() {
            @Override
            public boolean test(final MockTransformer<?> mockTransformer) {
                return klass.isAssignableFrom(mockTransformer.getClass());
            }
        };
    }
}
