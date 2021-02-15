package de.lystx.cloudapi.bukkit.utils;

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
        Class[] cl = new Class[classes.length + 1];
        for (int i = 0; i < classes.length; i++) {
            cl[i] = classes[i];
        }
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

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
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

    public static void sendPacket(Object packet) {
        Bukkit.getOnlinePlayers().forEach(player -> sendPacket(player, packet));
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
