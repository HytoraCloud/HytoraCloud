package de.lystx.cloudsystem.library.network.extra.exception;

/**
 * Error when a packet is received which can't be properly read.<br>
 * For example: No readable bytes; Wrong protocol version; Wrong packet id; ...
 */
public class BadPacketException extends Exception {

    public BadPacketException(String message) {
        super(message);
    }
}
