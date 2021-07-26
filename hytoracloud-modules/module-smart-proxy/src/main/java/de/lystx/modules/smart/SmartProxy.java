package de.lystx.modules.smart;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.ModuleInfo;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.DriverModule;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.cloudservices.global.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.enums.other.ModuleCopyType;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.modules.smart.commands.SmartProxyCommand;
import de.lystx.modules.smart.packet.MinecraftPacket;
import de.lystx.modules.smart.packet.PingPacket;
import de.lystx.modules.smart.proxy.ProxyDownstreamHandler;
import de.lystx.modules.smart.proxy.ProxyUpstreamHandler;
import de.lystx.modules.smart.server.*;
import de.lystx.modules.smart.utils.MinecraftState;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@ModuleInfo(
        name = "module-smart-proxy",
        authors = "Lystx",
        description = "This is a module to send players to proxies depending on the online count",
        version = "1.0",
        website = "https://hytora.cloud",
        main = SmartProxy.class,
        copyType = ModuleCopyType.COPY_NOT,
        allowedTypes = ServiceType.CLOUDSYSTEM
)

@Getter @Setter
public class SmartProxy extends DriverModule {

    //Config stuff
    private String proxySearchMode;
    private boolean enabled;

    //Packet registry and network stuff
    public static final Map<Integer, Class<? extends MinecraftPacket>> MINECRAFT_PACKETS = new HashMap<>();
    public final static AttributeKey<MinecraftState> CONNECTION_STATE = AttributeKey.valueOf("connectionstate");
    public final static AttributeKey<ProxyUpstreamHandler> UPSTREAM_HANDLER = AttributeKey.valueOf("upstreamhandler");
    public final static AttributeKey<ProxyDownstreamHandler> DOWNSTREAM_HANDLER = AttributeKey.valueOf("downstreamhandler");

    private ProxyNettyServer proxyNettyServer;
    private Channel channel;
    private EventLoopGroup workerGroup;

    //Instance
    @Getter
    private static SmartProxy instance;

    @Override
    public void onLoadConfig() {
        instance = this;

        this.enabled = this.config.getBoolean("enabled", true);
        this.proxySearchMode = this.config.getString("proxySearchMode", "RANDOM");
        this.config.save();

        NetworkConfig networkConfig = CloudDriver.getInstance().getNetworkConfig();
        if (enabled) {
            if (networkConfig.getProxyStartPort() == 25565) {
                CloudDriver.getInstance().log("SmartProxy", "§7Default-Proxy-Port was §b25565 §7had to change to §325566 §7in order to make §bSmartProxy §7work§h!");
                networkConfig.setProxyStartPort(25566);
            }

            MINECRAFT_PACKETS.put(0x00, PingPacket.class);
        } else {
            if (networkConfig.getProxyStartPort() != 25565) {
                CloudDriver.getInstance().log("SmartProxy", "§7System is currently §cdisabled§h! §7Setting Default-Proxy-Port §7back to §b25565§h!");
                networkConfig.setProxyStartPort(25565);
            }
        }

        networkConfig.update();
    }

    @Override
    public void onEnable() {
        if (this.enabled) {
            this.workerGroup = new NioEventLoopGroup();
            this.proxyNettyServer = new ProxyNettyServer("127.0.0.1", 25565);
            CloudDriver.getInstance().registerCommand(new SmartProxyCommand());
            try {
                proxyNettyServer.bind();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onReload() {
    }

    /**
     * Forwards a ping request to a {@link IService} and connects the requester to it
     *
     * @param state the login state
     * @param channel the netty channel
     * @param proxy the proxy
     * @param login the login packet as buf
     */
    public void forwardRequestToNextProxy(Channel channel, IService proxy, ByteBuf login, int state) {
        ProxyDownstreamHandler downstreamHandler = channel.attr(SmartProxy.DOWNSTREAM_HANDLER).get() == null ? new ProxyDownstreamHandler(channel) : channel.attr(SmartProxy.DOWNSTREAM_HANDLER).get();
        channel.attr(SmartProxy.DOWNSTREAM_HANDLER).set(downstreamHandler);
        channel.attr(SmartProxy.CONNECTION_STATE).set(MinecraftState.HANDSHAKE);

        new Bootstrap()
                .group(this.workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    public void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(downstreamHandler);
                    }
                }).connect(proxy.getAddress()).addListener((ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) {

                        if (channel.attr(SmartProxy.UPSTREAM_HANDLER).get() == null) {
                            ProxyUpstreamHandler upstreamHandler = new ProxyUpstreamHandler(channelFuture.channel(), downstreamHandler);
                            channel.pipeline().addLast(upstreamHandler);
                            channel.attr(SmartProxy.UPSTREAM_HANDLER).set(upstreamHandler);
                        } else {
                            channel.attr(SmartProxy.UPSTREAM_HANDLER).get().setChannel(channelFuture.channel());
                        }

                        if (channel.pipeline().get("minecraftdecoder") != null) {
                            channel.pipeline().remove("minecraftdecoder");
                        }

                        CloudDriver.getInstance().getMessageManager().sendChannelMessage(IChannelMessage.builder().channel("smart-proxy").key("PROXY_SET_IP").receiver(proxy).document(new JsonDocument().append("CLIENT_ADDRESS", channel.remoteAddress().toString()).append("CHANNEL_ADDRESS", channelFuture.channel().localAddress().toString())).build());
                        login.resetReaderIndex();
                        channelFuture.channel().writeAndFlush(login.retain());
                        channel.attr(SmartProxy.CONNECTION_STATE).set(MinecraftState.PROXY);
                    } else {
                        channel.close();
                        channelFuture.channel().close();
                    }
                });
    }

    /**
     * The last provided random service
     */
    private IService lastRandom;

    /**
     * Tries to find the best free proxy which is not already full
     * depending on your search mode you provided in the config
     *
     * 'RANDOM' will just search a random proxy
     * 'BALANCED' will try to balance all proxies
     * 'FILL' will try to fill all proxies
     *
     * @param group the group youre trying to get proxies of
     * @return service if found or null
     */
    public IService getFreeProxy(IServiceGroup group, int state) {
        if (group.getServices().size() == 1) {
            return group.getServices().get(0);
        }
        //Only free proxies
        IService value = null;
        List<IService> proxies = group.getServices().stream().filter(proxy -> proxy.getPlayers().size() < proxy.getGroup().getMaxPlayers()).collect(Collectors.toList());
        if (!proxies.isEmpty()) {
            if (this.proxySearchMode.equalsIgnoreCase("RANDOM")) {
                value = proxies.get(new Random().nextInt(proxies.size()));
            } else {
                proxies.sort(Comparator.comparing(service -> service.getPlayers().size()));
                if (this.proxySearchMode.equalsIgnoreCase("BALANCED")) {
                    value = proxies.get(0);
                } else {
                    value = proxies.get(proxies.size() - 1);
                }
            }
        }
        if (value != null) {
            if (lastRandom != null) {
                if (lastRandom.getName().equalsIgnoreCase(value.getName())) {
                    return getFreeProxy(group, state);
                } else {
                    if (state == 221) {
                        lastRandom = value;
                    }
                }
            } else {
                if (state == 221) {
                    lastRandom = value;
                }
            }
        }
        if (state == 349) {
            return lastRandom;
        }
        return value;
    }
}
