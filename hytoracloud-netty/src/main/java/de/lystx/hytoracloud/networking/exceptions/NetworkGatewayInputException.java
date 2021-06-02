package de.lystx.hytoracloud.networking.exceptions;

import de.lystx.hytoracloud.networking.packet.impl.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This exception is being called when the client receives a packets and the response is not OK
 */
@Getter @AllArgsConstructor
public class NetworkGatewayInputException extends Exception {

    private final Response response;

}
