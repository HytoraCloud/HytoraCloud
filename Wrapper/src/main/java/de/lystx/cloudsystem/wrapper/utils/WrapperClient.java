package de.lystx.cloudsystem.wrapper.utils;

import de.lystx.cloudsystem.library.service.network.connection.adapter.AdapterHandler;
import de.lystx.cloudsystem.library.service.network.connection.channel.base.NetworkChannel;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;

public class WrapperClient extends CloudClient {

    public WrapperClient(String host, Integer port, NetworkChannel networkChannel) {
        super(host, port, networkChannel, new AdapterHandler());
    }


}
