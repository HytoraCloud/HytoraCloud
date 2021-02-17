package de.lystx.cloudsystem.library.elements.other;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class ReceiverInfo implements Serializable {

    private final String name;
    private final String ipAddress;
    private final int port;
    private final boolean established;

}
