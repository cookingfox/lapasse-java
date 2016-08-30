package com.cookingfox.lapasse.samples.tasks_immutables;

import org.immutables.value.Value;

/**
 * Re-usable Immutables style. Not for abstract class implementations.
 */
@Value.Style(
        builderVisibility = Value.Style.BuilderVisibility.PACKAGE,
        visibility = Value.Style.ImplementationVisibility.PACKAGE
)
public @interface TasksStyle {
}
