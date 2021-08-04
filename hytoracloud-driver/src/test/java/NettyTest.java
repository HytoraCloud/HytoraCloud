
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.ClientType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.NetworkClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.INetworkClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.INetworkAdapter;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.PacketHandshake;
import de.lystx.hytoracloud.driver.connection.protocol.netty.server.INetworkServer;
import de.lystx.hytoracloud.driver.connection.protocol.netty.server.NetworkServer;
import io.netty.channel.Channel;

public class NettyTest {

    public static void main(String[] args) {

        INetworkServer networkServer = new NetworkServer("127.0.0.1", 2020);
        INetworkClient networkClient = new NetworkClient("127.0.0.1", 2020, ClientType.CUSTOM, "Lobby-1");


        networkServer.registerPacketHandler(new IPacketHandler() {
            @Override
            public void handle(IPacket packet) {
                System.out.println("[" + packet.getClass().getSimpleName() + "] " + packet.getDif() + "ms");
            }
        });

        networkClient.registerNetworkAdapter(new INetworkAdapter() {
            @Override
            public void onPacketReceive(IPacket packet) {
            }

            @Override
            public void onHandshakeReceive(PacketHandshake handshake) {
                networkClient.sendPacket(new TestPacket("Jonas", 11));
                networkClient.sendPacket(new SamplePacket("Jonas", 11));
            }

            @Override
            public void onPacketSend(IPacket packet) {
            }

            @Override
            public void onChannelActive(Channel channel) {
            }

            @Override
            public void onChannelInactive(Channel channel) {
            }
        });

        try {
            networkServer.bootstrap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            networkClient.bootstrap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
