package de.lystx.cloudsystem.library.network.extra.util;

import de.lystx.cloudsystem.library.network.extra.exception.NettyOutputException;

public class NettyUtil {

    /**
     * Checks if the current thread is an async moo pool task
     */
    public static void checkAsyncTask() {
        Thread currentThread = Thread.currentThread();
        if(currentThread.getName().equals("main")
                // I decided to not allow the nioEventLoopGroup as "async", because it could block
                // netty sending/receiving packets
                || currentThread.getName().startsWith("nioEventLoopGroup")) {
            throw new NettyOutputException(NettyOutputException.Type.WRONG_THREAD, currentThread.getName());
        }
    }

}
