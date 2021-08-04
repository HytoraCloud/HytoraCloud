package de.lystx.hytoracloud.driver.connection.protocol.netty.other;

/**
 * Error when a packet is received which can't be properly read.<br>
 * For example: No readable bytes; Wrong protocol version; Wrong packet id; ...
 */
public class BadPacketException extends Exception {

    private static final long serialVersionUID = 3230803854532897370L;

    public BadPacketException(String message) {
        super(message);
    }
}
