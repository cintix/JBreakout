/*
 */
package io.xml.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author migo
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(NamespaceContainer.class)

public @interface Namespace {
    public String name();
    public String uri();
    public boolean isDefault() default true;
}
