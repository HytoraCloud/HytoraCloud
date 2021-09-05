package de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.handler;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.api.base.component.NettyComponent;

public interface IRequestHandler<T> {

    /**
     * Handles this request
     *
     * @param request the request
     */
    void handle(NettyComponent<T> request);
}
