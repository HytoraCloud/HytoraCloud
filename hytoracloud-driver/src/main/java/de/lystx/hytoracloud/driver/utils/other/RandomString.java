package de.lystx.hytoracloud.driver.utils.other;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RandomString {

    /**
     * The random instance
     */
    private final Random random;

    /**
     * All symbols that are getting mxied
     */
    private final char[] symbols;

    /**
     * The char buf
     */
    private char[] buf;

    public RandomString(int length, Random random, String symbols) {
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    public RandomString(int length, Random random) {
        this(length, random, ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase(Locale.ROOT) + "0123456789"));
    }

    public RandomString(int length) {
        this(length, new SecureRandom());
    }

    /**
     * Returns a new Random String
     * with the length of the provided length
     * in the constructor
     * @return String
     */
    public String next() {
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }

    /**
     * Returns a new Random String
     * with the length of the provided length
     * as parameter
     * @return String
     */
    public String next(int newLength) {
        this.buf = new char[newLength];
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }

}