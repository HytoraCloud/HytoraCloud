package de.lystx.cloudsystem.library.service.util;

import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.UUID;

public class Reflections {

    @SneakyThrows
    public static Class<?> getNmsClass(String nmsClassName) {
        return Class.forName("net.minecraft.server." + Constants.BUKKIT_VERSION + "." + nmsClassName);
    }

    /**
     * Returns the UUID of a Player Object
     * via Reflections to use it in Library without
     * having Bukkit dependency
     *
     * @param player
     * @return
     */
    @SneakyThrows
    public static UUID getUniqueId(Object player) {
        Class<?> craftHumanEntity = player.getClass().getSuperclass();
        Class<?> craftLivingEntity = craftHumanEntity.getSuperclass();
        Class<?> craftEntity = craftLivingEntity.getSuperclass();

        final Method declaredMethod = craftEntity.getDeclaredMethod("getUniqueId");
        declaredMethod.setAccessible(true);
        return (UUID) declaredMethod.invoke(player);
    }


    /**
     * Returns a Player Object from Bukkit
     * with the name of the player
     * via Reflections
     * @param name
     * @return
     */
    @SneakyThrows
    public static Object getBukkitPlayer(String name) {
        Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
        final Method getPlayer = bukkitClass.getDeclaredMethod("getPlayer", String.class);
        getPlayer.setAccessible(true);
        return getPlayer.invoke(bukkitClass, name);
    }

    /**
     * Returns a Player Object from Bukkit
     * with the name of the player
     * via Reflections
     * @param name
     * @return
     */
    @SneakyThrows
    public static Object getBungeePlayer(String name) {
        final Class<?> bungeeClass = Class.forName("net.md_5.bungee.api.ProxyServer");
        final Method getInstance = bungeeClass.getDeclaredMethod("getInstance"); getInstance.setAccessible(true);
        final Object proxyServer = getInstance.invoke(bungeeClass);
        final Method getPlayer = proxyServer.getClass().getDeclaredMethod("getPlayer", String.class); getPlayer.setAccessible(true);
        return getPlayer.invoke(proxyServer, name);
    }

    /**
     * Calls a Method via Reflections
     * @param name Name of the Method
     * @param args Arguments (Parameters) for the method
     */
    @SneakyThrows
    public static void callMethod(Object object, String name, Object... args) {
        final Method declaredMethod = object.getClass().getDeclaredMethod(name, toArray(args));
        declaredMethod.setAccessible(true);
        declaredMethod.invoke(object, args);
    }

    /**
     * Sends a packet to Bukkit player
     * @param player
     * @param packet
     */
    public static void sendPacket(Object player, Object packet) {
        try {
            Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            playerConnection.getClass().getMethod("sendPacket", getNmsClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    @SneakyThrows
    public static Method getMethod(Class<?> aClass, String name, Object... args) {
        Method method;
        try {
            method = aClass.getMethod(name, toArray(args));
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            method = getMethod(aClass.getSuperclass(), name, args);
        }
        return method;
    }

    public static Class<?>[] toArray(Object... args) {
        Class<?>[] ps = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            ps[i] = args[i].getClass();
        }
        return ps;
    }
}
