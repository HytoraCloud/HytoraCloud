package de.lystx.cloudsystem.library.network.extra.exception;

import de.lystx.cloudsystem.library.network.packet.response.Response;
import lombok.Getter;

/**
 * This exception is being called when the client receives a packets and the response is not OK
 */
@Getter
public class NettyInputException extends Exception {

    private final Response response;

    public NettyInputException(Response response) {
        this.response = response;
    }

}
