package org.powermock.modules.agent;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class AbstractClassTransformerTest {

    @Test public void
    replace_slash_with_dots_method_replaces_slashes_with_dots() {
        // Given
        AbstractClassTransformer abstractClassTransformer = new AbstractClassTransformer() { };

        // When
        String replaced = abstractClassTransformer.replaceSlashWithDots("org/powermock/core/Main");

        // Then
        assertThat(replaced, equalTo("org.powermock.core.Main"));
    }
}