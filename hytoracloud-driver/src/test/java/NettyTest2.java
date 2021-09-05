import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.component.NettyComponent;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.future.NettyFuture;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.future.NettyFutureListener;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.channel.INetworkChannel;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.INetworkClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.NetworkClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.handler.IRequestHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.identification.ConnectionType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.handling.INetworkAdapter;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.PacketHandshake;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.response.PacketRespond;
import de.lystx.hytoracloud.driver.connection.protocol.netty.server.INetworkServer;
import de.lystx.hytoracloud.driver.connection.protocol.netty.server.NetworkServer;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.response.ResponseStatus;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;

import java.util.LinkedList;

public class NettyTest2 {

    public static void main(String[] args) {

        INetworkServer networkServer = new NetworkServer("127.0.0.1", 2020, ConnectionType.CLOUD_INSTANCE);
        INetworkClient networkClient = new NetworkClient("127.0.0.1", 2020, ConnectionType.JUST_CLIENT, "Lobby-1");

        networkServer.getRequestManager().registerRequestHandler(new IRequestHandler<String>() {
            @Override
            public void handle(NettyComponent<String> request) {

                request.createResponse(String.class).value("Test Response").post(networkServer);
            }
        });

        networkClient.registerNetworkAdapter(new INetworkAdapter() {
            @Override
            public void onHandshakeReceive(PacketHandshake handshake) {

                NettyComponent<String> request = NettyComponent.request(String.class).key("TEST_KEY").document(JsonObject.serializable());

                NettyFuture<String> stringNettyFuture = request.queryRequest(networkClient)
                        .addListener(new NettyFutureListener<String>() {
                            @Override
                            public void handle(NettyFuture<String> nettyFuture) {
                                if (nettyFuture.isSuccess()) {
                                    System.out.println(nettyFuture.getCompletionTimeMillis() + "ms");
                                    System.out.println(nettyFuture.pullValue());
                                } else {
                                    nettyFuture.getError().printStackTrace();
                                }
                            }
                        });


               // System.out.println(stringNettyFuture.pullValue());

            }

            @Override
            public void onChannelActive(INetworkChannel channel) {
            }

            @Override
            public void onChannelInactive(INetworkChannel channel) {
            }

            @Override
            public void onPacketSend(IPacket packet) {
            }

            @Override
            public void onPacketReceive(IPacket packet) {
            }
        });


        new Thread(networkServer::bootstrap).start();
        new Thread(networkClient::bootstrap).start();

        System.out.println("Done.");
    }
}
