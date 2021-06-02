package de.lystx.hytoracloud.networking.exceptions;

/**
 * Error when a packet is received which can't be properly read.<br>
 * For example: No readable bytes; Wrong protocol version; Wrong packet id; ...
 */
public class NetworkBadPacketException extends Exception {

    public NetworkBadPacketException(String message) {
        super(message);
    }
}
