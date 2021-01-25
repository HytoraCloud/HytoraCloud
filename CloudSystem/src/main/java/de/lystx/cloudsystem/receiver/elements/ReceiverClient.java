package de.lystx.cloudsystem.receiver.elements;

import de.lystx.cloudsystem.library.service.network.connection.adapter.AdapterHandler;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;

public class ReceiverClient extends CloudClient {

    public ReceiverClient(String host, Integer port, NetworkChannel networkChannel) {
        super(host, port, networkChannel, new AdapterHandler());
    }


}
