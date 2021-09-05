package de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.future;

public interface NettyFutureListener<T> {

    /**
     * Handles the given {@link NettyFuture}
     *
     * @param nettyFuture the future
     */
    void handle(NettyFuture<T> nettyFuture);
}
