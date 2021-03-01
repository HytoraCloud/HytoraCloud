package de.lystx.cloudsystem.library.service.util;

import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import io.netty.channel.epoll.Epoll;

public class Constants {

    public static CloudExecutor EXECUTOR = null;
    public static PermissionPool PERMISSION_POOL = null;

    public static boolean NEEDS_DEPENDENCIES;
    public static boolean NEEDS_DEPENDENCIES_2;

    static {
        try {
            Class.forName("jline.console.ConsoleReader");
            NEEDS_DEPENDENCIES = false;
        } catch (ClassNotFoundException e) {
            NEEDS_DEPENDENCIES =  true;
        }
        try {
            Class.forName("jline.console.completer.Completer");
            NEEDS_DEPENDENCIES_2 = false;
        } catch (ClassNotFoundException e) {
            NEEDS_DEPENDENCIES_2 =  true;
        }
    }
}
