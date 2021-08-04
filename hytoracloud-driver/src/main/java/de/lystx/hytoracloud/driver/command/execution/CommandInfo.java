package de.lystx.hytoracloud.driver.command.execution;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {

    /**
     * The name of the command
     * @return name as String
     */
    String name();

    /**
     * The description of the command
     * @return description as String
     */
    String description() default "";

    /**
     * The aliases of the command
     * @return aliases as String-Array
     */
    String[] aliases() default {};

}

