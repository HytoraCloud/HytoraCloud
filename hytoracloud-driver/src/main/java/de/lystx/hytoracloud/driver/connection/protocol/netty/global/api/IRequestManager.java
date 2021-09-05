package de.lystx.hytoracloud.driver.connection.protocol.netty.global.api;

import de.lystx.hytoracloud.driver.connection.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.component.NettyComponent;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.future.NettyFuture;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.handler.IRequestHandler;


public interface IRequestManager {

    /**
     * Registers an {@link IRequestHandler}
     *
     * @param handler the handler
     */
    void registerRequestHandler(IRequestHandler<?> handler);

    /**
     * Unregisters an {@link IRequestHandler}
     *
     * @param handler the handler
     */
    void unregisterRequestHandler(IRequestHandler<?> handler);

    /**
     * Adds a {@link NettyFuture} to the cache with a given id
     *
     * @param id the id of the request
     * @param future the future
     */
    void addRequest(String id, NettyFuture<?> future);

    /**
     * Gets an {@link NettyFuture} from cache
     * and then automatically removes it from cache
     *
     * @param id the id
     * @return future or null
     */
    NettyFuture<?> retrieveFuture(String id);

    /**
     * Transforms an {@link NettyComponent} to a {@link IChannelMessage}
     * to be able to communicate around the network
     *
     * @param request the request
     * @return message
     */
    IChannelMessage toMessage(NettyComponent<?> request);

}
