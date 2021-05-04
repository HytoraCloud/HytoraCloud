package de.lystx.cloudsystem.library.network.packet.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ResponseData {

    private final ResponseStatus status;
    private final String message;
}
