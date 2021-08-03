package de.lystx.modules.smart;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.module.base.info.ModuleInfo;
import de.lystx.hytoracloud.driver.module.base.ModuleState;
import de.lystx.hytoracloud.driver.module.base.info.ModuleTask;
import de.lystx.hytoracloud.driver.module.cloud.DriverModule;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.utils.enums.other.ModuleCopyType;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.modules.smart.commands.SmartProxyCommand;
import de.lystx.modules.smart.packet.MinecraftPacket;
import de.lystx.modules.smart.packet.PingPacket;
import de.lystx.modules.smart.proxy.ForwardDownStream;
import de.lystx.modules.smart.proxy.ForwardUpStream;
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
        copyType = ModuleCopyType.NOT,
        allowedTypes = ServerEnvironment.CLOUD
)

@Getter @Setter
public class SmartProxy extends DriverModule {

    private static final long serialVersionUID = -7805187373620103010L;

    //Config stuff
    /**
     * The searching mode for a free proxy
     */
    private String proxySearchMode;

    /**
     * If module is enabled
     */
    private boolean enabled;

    /**
     * All registered {@link MinecraftPacket}s
     */
    public static final Map<Integer, Class<? extends MinecraftPacket>> MINECRAFT_PACKETS = new HashMap<>();

    /**
     * The connection states
     */
    public final static AttributeKey<MinecraftState> CONNECTION_STATE = AttributeKey.valueOf("connectionstate");

    /**
     * The {@link ForwardDownStream}s
     */
    public final static AttributeKey<ForwardDownStream> FORWARDING_DOWN = AttributeKey.valueOf("downstreamhandler");

    /**
     * The {@link ForwardUpStream}s
     */
    public final static AttributeKey<ForwardUpStream> FORWARDING_UP = AttributeKey.valueOf("upstreamhandler");

    /**
     * The netty server
     */
    private ProxyNettyServer proxyNettyServer;

    /**
     * The netty channel
     */
    private Channel channel;

    /**
     * The netty worker group
     */
    private EventLoopGroup workerGroup;

    /**
     * The static instance
     */
    @Getter
    private static SmartProxy instance;

    @ModuleTask(id = 1, state = ModuleState.LOADING)
    public void loadConfig() {
        instance = this;

        this.enabled = this.config.def(true).getBoolean("enabled");
        this.proxySearchMode = this.config.def("RANDOM").getString("proxySearchMode");
        this.config.save();

        NetworkConfig networkConfig = CloudDriver.getInstance().getConfigManager().getNetworkConfig();
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

    @ModuleTask(id = 2, state = ModuleState.STARTING)
    public void startModule() {
        if (this.enabled) {
            this.workerGroup = new NioEventLoopGroup();
            this.proxyNettyServer = new ProxyNettyServer("127.0.0.1", 25565);
            CloudDriver.getInstance().getCommandManager().registerCommand(new SmartProxyCommand());
            try {
                proxyNettyServer.bind();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @ModuleTask(id = 3, state = ModuleState.STOPPING)
    public void stopModule() {
        this.proxyNettyServer.unbind();
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
        ForwardDownStream downstreamHandler = channel.attr(SmartProxy.FORWARDING_DOWN).get() == null ? new ForwardDownStream(channel) : channel.attr(SmartProxy.FORWARDING_DOWN).get();
        channel.attr(SmartProxy.FORWARDING_DOWN).set(downstreamHandler);
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

                        if (channel.attr(SmartProxy.FORWARDING_UP).get() == null) {
                            ForwardUpStream upstreamHandler = new ForwardUpStream(channelFuture.channel(), downstreamHandler);
                            channel.pipeline().addLast(upstreamHandler);
                            channel.attr(SmartProxy.FORWARDING_UP).set(upstreamHandler);
                        } else {
                            channel.attr(SmartProxy.FORWARDING_UP).get().setChannel(channelFuture.channel());
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
                    for (int i = proxies.size() - 1; i >= 0; i--) {
                        IService server = proxies.get(i);
                        if (server.getPlayers().size() < server.getGroup().getMaxPlayers()) {
                            value = server;
                            break;
                        }
                    }
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
