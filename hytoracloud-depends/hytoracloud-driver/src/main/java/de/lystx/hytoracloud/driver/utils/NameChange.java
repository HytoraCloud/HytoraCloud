package de.lystx.hytoracloud.driver.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


@AllArgsConstructor @Getter
public class NameChange implements Serializable {

    private static final long serialVersionUID = -988078776774697230L;

    /**
     * The name of this change
     */
    private final String name;

    /**
     * The time when it was changed
     */
    private final Long time;

    /**
     * If this was the original name
     */
    private final boolean firstName;


    /**
     * Formats this nameChange with a default Date-Pattern
     *
     * @return string
     */
    public String format() {
        return this.format("dd.MM.yyyy - hh:mm:ss");
    }

    /**
     * Formats this nameChange with a given Date-Pattern
     *
     * @param pattern the pattern
     * @return string
     */
    public String format(String pattern) {
        return (name + " - ") + (this.firstName ? "Original" : new SimpleDateFormat(pattern).format(new Date(time)));
    }
}
