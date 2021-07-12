package de.lystx.hytoracloud.driver.cloudservices.managing.command.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

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

    /**
     * The usage of the command
     *
     * @return usage
     */
    CommandUsage usage() default @CommandUsage(exactArgs = -1, trigger = "null", usage = {});
}

