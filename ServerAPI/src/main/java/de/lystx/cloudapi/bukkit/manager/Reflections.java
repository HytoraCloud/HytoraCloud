package de.lystx.cloudapi.bukkit.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflections {


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


    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (Exception e) {
            return null;
        }
    }

    public static Class<?> getCustomClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName(name.replace("%d%", version));
        } catch (Exception e) {
            return null;
        }
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

    public Object getPacket() {
        return new Object();
    }

    public Object getPlayerConnection(Player player) {

        try {
            Method getHandle = player.getClass().getMethod("getHandle", (Class<?>[]) null);
            Object entityPlayer = getHandle.invoke(player);
            return entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPacket(Player to, Object packet) {
        try {
            Object playerHandle = to.getClass().getMethod("getHandle", new Class[0]).invoke(to);
            Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
            playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
