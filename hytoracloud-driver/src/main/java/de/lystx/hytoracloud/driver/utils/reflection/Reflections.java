package de.lystx.hytoracloud.driver.utils.reflection;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class Reflections {

    @SneakyThrows
    public static Class<?> getNmsClass(String nmsClassName) {
        return Class.forName("net.minecraft.server." + CloudDriver.getInstance().getBukkit().getVersion() + "." + nmsClassName);
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


    public static Object getPlayer(String name) {
        if (CloudDriver.getInstance().getCurrentService().getGroup().getType() == ServiceType.SPIGOT) {
            return getBukkitPlayer(name);
        } else {
            ProxyVersion version = CloudDriver.getInstance().getProxyBridge().getVersion();
            if (version.equals(ProxyVersion.BUNGEECORD) || version.equals(ProxyVersion.WATERFALL)) {
                return getBungeePlayer(name);
            } else if (version.equals(ProxyVersion.VELOCITY)) {
                return getVelocityPlayer(name);
            }
        }
        return null;
    }



    /**
     * Returns a Player Object from velocity
     * with the name of the player
     * via Reflections
     * @param name the name of the player
     * @return  player object
     */
    @SneakyThrows
    public static Object getVelocityPlayer(String name) {
        final Class<?> bridgeClass = Class.forName("de.lystx.hytoracloud.bridge.velocity.HytoraCloudVelocityBridge");
        final Method getInstance = bridgeClass.getDeclaredMethod("getInstance"); getInstance.setAccessible(true);

        final Object instance = getInstance.invoke(bridgeClass);

        Method getServer = instance.getClass().getDeclaredMethod("getServer"); getServer.setAccessible(true);

        Object server = getServer.invoke(instance);

        final Method getPlayer = server.getClass().getDeclaredMethod("getPlayer", String.class); getPlayer.setAccessible(true);
        return getPlayer.invoke(server, name);
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
    public static Object callMethod(Object object, String name, Object... args) {
        final Method declaredMethod = object.getClass().getDeclaredMethod(name, toArray(args));
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(object, args);
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


    public static void setField(Object change, String name, Object to)  {
        try {
            Field field = null;
            field = change.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(change, to);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void invoke(Object invoke, String methodName, Object... args) {
        Class<?>[] clazz = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            clazz[i] = args[i].getClass();
        }
        try {
            invoke.getClass().getMethod(methodName, clazz).invoke(invoke, args);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static <T> void invokeUnsafe(Object invoke, String methodName, Class<?>[] classes, Class<T> tClass, Object... args) {
        int max = classes.length;
        Class<?>[] cl = new Class[classes.length + 1];
        System.arraycopy(classes, 0, cl, 0, classes.length);
        cl[max] = tClass;
        try {
            invoke.getClass().getMethod(methodName, cl).invoke(invoke, args);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static Enum<?> getEnum(Class<?> clazz, int classID, int enumID) {
        Class<?> enumClass;
        try {
            enumClass = clazz.getDeclaredClasses()[classID];
        } catch (ArrayIndexOutOfBoundsException e) {
            enumClass = clazz.getDeclaredClasses()[0];
            //System.out.println("[Reflections] IndexOutOfBound : " + classID + " but max for class " + clazz.getSimpleName() + " is " + clazz.getDeclaredClasses().length + "!");
        }
        Enum<?>[] enumConstants = (Enum<?>[]) enumClass.getEnumConstants();

        return enumConstants[enumID];
    }

    public static Class<?> getClass(Class<?> clazz, int classID) {
        return clazz.getDeclaredClasses()[classID];
    }

    public static Object[] fromIChatMessage(String input) {
        try {
            Method method = Reflections.getCustomClass("org.bukkit.craftbukkit.%d%.util.CraftChatMessage").getMethod("fromString", String.class);
            return (Object[]) method.invoke(null, input);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }


    public static Class<?> getNMSClass(String name) {
        String version = CloudDriver.getInstance().getBukkit().getVersion();
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (Exception e) {
            return null;
        }
    }

    public static Class<?> getCustomClass(String name) {
        String version = CloudDriver.getInstance().getBukkit().getVersion();
        try {
            return Class.forName(name.replace("%d%", version));
        } catch (Exception e) {
            return null;
        }
    }

    public static String getVersion() {
        return CloudDriver.getInstance().getBukkit().getVersion();
    }

    public static Class<?> getCraftBukkitClass(String name) {
        return getCustomClass("org.bukkit.craftbukkit.%d%." + name);
    }

    public static Object getField(String fieldname, String clazz, String instanceMethod) {
        try {
            Class<?> cs = getNMSClass(clazz);
            return cs.getDeclaredField(fieldname).get(getInstance(clazz, instanceMethod));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getInstance(String clazz, String instanceMethod) {
        try {
            Class<?> cs = getNMSClass(clazz);
            if (instanceMethod.trim().isEmpty()) {
                return cs.getConstructor().newInstance();
            }
            return cs.getMethod(instanceMethod).invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getInstance(Object from, String instanceMethod) {
        try {
            return from.getClass().getMethod(instanceMethod).invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void setValue(Object obj, String name, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {}
    }

    public static Object getValue(Object obj, String name) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {}
        return null;
    }

    public Object getPacket() {
        return new Object();
    }


}
