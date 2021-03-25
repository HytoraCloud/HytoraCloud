package de.lystx.cloudsystem.library.service.setup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to declare to which question
 * to go if a certain answer is something
 * you'd like to trigger
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GoTo {

    int id(); //ID IF VALUE IS GIVEN

    int elseID(); //ID IF VALUE IS NOT GIVE

    String value(); //THE VALUE
}
