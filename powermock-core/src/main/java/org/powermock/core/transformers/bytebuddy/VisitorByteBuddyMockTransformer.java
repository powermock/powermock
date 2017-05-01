/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.transformers.bytebuddy;

import net.bytebuddy.asm.AsmVisitorWrapper;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.bytebuddy.support.ByteBuddyClass;

public abstract class VisitorByteBuddyMockTransformer extends AbstractByteBuddyMockTransformer {
    
    protected VisitorByteBuddyMockTransformer(final TransformStrategy strategy) {
        super(strategy);
    }
    
    protected ByteBuddyClass visit(final ByteBuddyClass clazz, final AsmVisitorWrapper asmVisitorWrapper) {
        return new ByteBuddyClass(
                                     clazz.getTypeDefinitions(),
                                     clazz.getBuilder().visit(asmVisitorWrapper)
        );
    }
}
