package de.lystx.cloudsystem.library.service.random;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RandomInteger {

    private final Random random;
    private final char[] symbols;
    private char[] buf;

    public RandomInteger(int length, Random random) {
        this.random = Objects.requireNonNull(random);
        this.symbols = "0123456789".toCharArray();
        this.buf = new char[length];
    }

    public RandomInteger(int length) {
        this(length, new SecureRandom());
    }

    public int next() {
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return Integer.parseInt(new String(buf));
    }

    public int next(int newLength) {
        this.buf = new char[newLength];
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return Integer.parseInt(new String(buf));
    }

}