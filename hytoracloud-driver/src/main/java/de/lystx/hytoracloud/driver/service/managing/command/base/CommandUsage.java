package de.lystx.hytoracloud.driver.service.managing.command.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandUsage {

    /**
     * The length of the args to use
     *
     * @return int
     */
    int exactArgs() default -1;

    /**
     * Checks if arguments are not the type
     *
     * @return int
     */
    int notArgs() default -1;

    /**
     * If the method should be invoked although
     * the usage was not successfully passed
     *
     * @return boolean
     */
    boolean invokeAnyways() default false;

    /**
     * Checks if arguments are minimum the type
     *
     * @return int
     */
    int minArgs() default -1;

    /**
     * Checks if arguments are maximum the type
     *
     * @return int
     */
    int maxArgs() default -1;

    /**
     * The string to trigger the usage and its position
     *
     * @return arg
     */
    String[] trigger() default {"example", "position as integer"};

    /**
     * The usage to send
     *
     * @return array
     */
    String[] usage();
}

