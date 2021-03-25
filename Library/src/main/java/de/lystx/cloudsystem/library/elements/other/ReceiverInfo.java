package de.lystx.cloudsystem.library.elements.other;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

/**
 * This class stores all the data on a Receiver
 * for example the name of it, the ip of the cloud etc...
 *
 * The values Map is for storing extra data for later updates
 * so the whole config will not be broken
 */
@Getter @AllArgsConstructor
public class ReceiverInfo implements Serializable {

    private final String name;
    private final String ipAddress;
    private final int port;
    private final boolean established;
    private final Map<String, Object> values;

}
