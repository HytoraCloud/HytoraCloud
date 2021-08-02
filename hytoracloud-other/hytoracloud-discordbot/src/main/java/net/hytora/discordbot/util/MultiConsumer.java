package net.hytora.discordbot.util;

public interface MultiConsumer<RETURN, F, S, T> {


    /**
     * Accepts three values
     *
     * @param f the first
     * @param s the second
     * @param t the third
     */
    RETURN accept(F f, S s, T t);
}
