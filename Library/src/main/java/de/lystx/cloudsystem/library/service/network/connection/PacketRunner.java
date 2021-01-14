package de.lystx.cloudsystem.library.service.network.connection;

import de.lystx.cloudsystem.library.service.network.connection.channel.base.Channel;

import java.net.Socket;

public interface PacketRunner {
    void run(Channel paramChannel, Socket paramSocket);
}
