package net.hytora.networking.elements.packet.response;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * Status of a packet process which can be either positive or negative
 * Similar to the HTTP responses
 */
@AllArgsConstructor @Getter
public enum ResponseStatus {

    /**
     * Everything went good
     */
    SUCCESS(0x00),

    /**
     * The operation has failed
     */
    FAILED(0x40),

    /**
     * Neutral
     */
    NONE(0x41),

    /**
     * You are not allowed
     * to perform a given action
     */
    FORBIDDEN(0x42),

    /**
     * There was a conflic
     */
    CONFLICT(0x43),

    /**
     * Something was not found
     */
    NOT_FOUND(0x44);

    @Getter
    private final int id;

}
