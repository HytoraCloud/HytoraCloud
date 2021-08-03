package de.lystx.modules.smart.server;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.modules.smart.SmartProxy;
import de.lystx.modules.smart.packet.PacketBuffer;
import de.lystx.modules.smart.packet.MinecraftPacket;
import de.lystx.modules.smart.packet.PingPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;


@ChannelHandler.Sharable
public class PacketDecoder extends SimpleChannelInboundHandler<ByteBuf> {

    public PacketDecoder() {
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {

            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();

            PacketBuffer buffer = new PacketBuffer(buf);
            int packetLength = buffer.readSignedVarInt();
            int packetID = buffer.readSignedVarInt();

            Class<? extends MinecraftPacket> aClass = SmartProxy.MINECRAFT_PACKETS.get(packetID);

            if (aClass == null) {
                //CloudDriver.getInstance().log("SmartProxy", "§cReceived Packet with id §e" + packetID + " §cthat is not registered!");
                return;
            }
            MinecraftPacket minecraftPacket = aClass.newInstance();

            minecraftPacket.read(buffer);
            minecraftPacket.handle();
            buf.retain();

            if (minecraftPacket instanceof PingPacket) {
                PingPacket pingPacket = (PingPacket) minecraftPacket;

                String hostName = pingPacket.getHostName();

                hostName = hostName.replace("localhost", "127.0.0.1");
                hostName = hostName.replace("192.168.178.82", "127.0.0.1");

                List<IService> proxies = CloudDriver.getInstance().getServiceManager().getCachedObjects(ServerEnvironment.PROXY);
                IService service = proxies.get(new Random().nextInt(proxies.size()));

                if (service != null) {
                    IService freeProxy = SmartProxy.getInstance().getFreeProxy(service.getGroup(), pingPacket.getState());
                    if (freeProxy == null) {
                        CloudDriver.getInstance().log("SmartProxy", "§cNo free Proxy for group §e" + service.getGroup().getName() + " could be found. Shutting down...");
                        ctx.channel().close();
                        return;
                    }
                    SmartProxy.getInstance().forwardRequestToNextProxy(ctx.channel(), freeProxy, buf, pingPacket.getState());
                }
            }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //Ignoring exceptions at first
    }

}
