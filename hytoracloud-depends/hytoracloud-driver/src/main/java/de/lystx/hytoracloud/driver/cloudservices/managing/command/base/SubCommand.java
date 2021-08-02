package de.lystx.hytoracloud.driver.cloudservices.managing.command.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {

    /**
     * The valid args for this sub command
     *
     * @return array
     */
    String[] validArgs();

    /**
     * The needed args length
     *
     * @return int
     */
    int neededArgs();
}

