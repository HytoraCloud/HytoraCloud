package de.lystx.hytoracloud.driver.service.util.random;

import lombok.Getter;

@Getter
public class Random {

    private static Random instance;

    private final RandomString string;
    private final RandomInteger integer;

    /**
     * Creates the Attributes
     */
    public Random() {
        this.string = new RandomString(10);
        this.integer = new RandomInteger(1);
    }

    /**
     * Returns the instance
     * @return
     */
    public static Random current() {
        if (instance == null) {
            instance = new Random();
        }
        return instance;
    }
}
