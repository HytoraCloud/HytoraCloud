package de.lystx.hytoracloud.driver.utils.uuid;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;


@AllArgsConstructor @Getter
public class NameChange {

    private final String name;
    private final Long time;
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
