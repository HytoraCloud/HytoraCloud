package de.lystx.cloudsystem.library.service.random;

import lombok.Getter;

@Getter
public class Random {

    private static Random instance;

    private final RandomString string;
    private final RandomInteger integer;


    public Random() {
        this.string = new RandomString(10);
        this.integer = new RandomInteger(1);
    }

    public static Random current() {
        if (instance == null) {
            instance = new Random();
        }
        return instance;
    }
}
