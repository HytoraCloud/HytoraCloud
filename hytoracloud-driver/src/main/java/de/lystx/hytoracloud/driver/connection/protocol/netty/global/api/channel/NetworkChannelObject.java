package de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.channel;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.SocketAddress;

@Getter @AllArgsConstructor
public class NetworkChannelObject implements INetworkChannel {

    private final INetworkConnection networkConnection;
    private final Channel channel;

    @Override
    public void sendPacket(IPacket packet) {
        this.networkConnection.getNetworkBus().processOut(this.channel, packet);
    }

    @Override
    public boolean isOpen() {
        return this.channel.isOpen();
    }

    @Override
    public boolean isRegistered() {
        return this.channel.isRegistered();
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }

    @Override
    public SocketAddress localAddress() {
        return this.channel.localAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return this.channel.remoteAddress();
    }

    @Override
    public boolean isWritable() {
        return this.channel.isWritable();
    }

    @Override
    public INetworkConnection parent() {
        return this.networkConnection;
    }

    @Override
    public INetworkChannel read() {
        this.channel.read();
        return this;
    }

    @Override
    public INetworkChannel writeAndFlush(Object obj) {
        this.channel.writeAndFlush(obj);
        return this;
    }

    @Override
    public INetworkChannel write(Object obj) {
        this.channel.write(obj);
        return this;
    }

    @Override
    public ChannelFuture closeFuture() {
        return channel.closeFuture();
    }

    @Override
    public Channel nettyVariant() {
        return this.channel;
    }

    @Override
    public void disconnect() {
        this.channel.disconnect();
    }

    @Override
    public void deregister() {
        this.channel.deregister();
    }

    @Override
    public void close() {
        this.channel.close();
    }

    @Override
    public INetworkChannel flush() {
        this.channel.flush();
        return this;
    }
}
