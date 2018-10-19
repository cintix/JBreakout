/*
 */
package io.xml.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author migo
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Alias {
    public String name();
}
