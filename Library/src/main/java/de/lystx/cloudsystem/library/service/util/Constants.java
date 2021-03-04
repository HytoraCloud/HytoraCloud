package de.lystx.cloudsystem.library.service.util;

import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;

public class Constants {

    public static CloudExecutor EXECUTOR = null;
    public static PermissionPool PERMISSION_POOL = null;

    public static boolean NEEDS_DEPENDENCIES;
    public static boolean JLINE_COMPLETER_INSTALLED;

    /**
     * Checks if dependencies are already loaded
     */
    static {
        try {
            Class.forName("jline.console.ConsoleReader");
            NEEDS_DEPENDENCIES = false;
        } catch (ClassNotFoundException e) {
            NEEDS_DEPENDENCIES =  true;
        }
        try {
            Class.forName("jline.console.completer.Completer");
            JLINE_COMPLETER_INSTALLED = false;
        } catch (ClassNotFoundException e) {
            JLINE_COMPLETER_INSTALLED =  true;
        }
    }
}
