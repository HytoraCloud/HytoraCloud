package de.lystx.cloudsystem.library.network.extra.exception;

import lombok.Getter;

/**
 * This exception is being called when:<br>
 * - The client tries to send a packets not inside the moo-pool executor service<br>
 * - The client tries to send a packets without cloud-connection
 */
public class NettyOutputException extends RuntimeException {

    @Getter
    private Type type;

    public NettyOutputException(Type type, Object... replacements) {
        super(String.format(type.getMessage(), replacements));
        this.type = type;
    }

    /**
     * The type of the exception earlier mentioned before
     */
    public enum Type {

        CONNECTION_FAILED,
        WRONG_THREAD("You have to execute packet operations asynchronous! ({0})");

        @Getter
        private String message;

        Type(String message) {
            this.message = message;
        }

        Type() {
        }

    }

}
