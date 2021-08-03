package de.lystx.hytoracloud.driver.utils.other;

import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class Reflections {

    /**
     * Returns the UUID of a Player Object
     * via Reflections to use it in Library without
     * having Bukkit dependency
     *
     * @param player the player object
     * @return uuid
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
     *
     * @param name the name
     * @return bukkit player object
     */
    @SneakyThrows
    public static Object getBukkitPlayer(String name) {
        Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
        final Method getPlayer = bukkitClass.getDeclaredMethod("getPlayer", String.class);
        getPlayer.setAccessible(true);
        return getPlayer.invoke(bukkitClass, name);
    }

    /**
     * Calls a Method via Reflections
     *
     * @param name Name of the Method
     * @param args Arguments (Parameters) for the method
     */
    @SneakyThrows
    public static void callMethod(Object object, String name, Object... args) {
        Method declaredMethod = object.getClass().getDeclaredMethod(name, toArray(args));
        declaredMethod.setAccessible(true);
        declaredMethod.invoke(object, args);
    }

    /**
     * Sends a packet to Bukkit player
     *
     * @param to the receiver
     * @param packet the packet
     */
    public static void sendPacket(Object to, Object packet) {
        try {
            Object playerHandle = to.getClass().getMethod("getHandle", new Class[0]).invoke(to);
            Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
            playerConnection.getClass().getMethod("sendPacket", new Class[] { Reflections.getNMSClass("Packet") }).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Transforms objects into a class-array
     *
     * @param args the args
     * @return array
     */
    public static Class<?>[] toArray(Object... args) {
        Class<?>[] ps = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            ps[i] = args[i].getClass();
        }
        return ps;
    }


    /**
     * Sets a field of an object
     *
     * @param change the object
     * @param name the name of the field
     * @param to the changed value
     */
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

    /**
     * Gets a NMS Class for a name
     *
     * @param name the name
     * @return class or null
     */
    public static Class<?> getNMSClass(String name) {
        String version = CloudDriver.getInstance().getBukkit().getVersion();
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets a custom bukkit class
     * @param name the name
     * @return class or null
     */
    public static Class<?> getCustomClass(String name) {
        String version = CloudDriver.getInstance().getBukkit().getVersion();
        try {
            return Class.forName(name.replace("%d%", version));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets a craft bukkit class by name
     *
     * @param name the name
     * @return class or null
     */
    public static Class<?> getCraftBukkitClass(String name) {
        return getCustomClass("org.bukkit.craftbukkit.%d%." + name);
    }

    /**
     * Gets a field by an instance method
     *
     * @param fieldname the field name
     * @param clazz the class
     * @param instanceMethod the instance method
     * @return object or null
     */
    public static Object getField(String fieldname, String clazz, String instanceMethod) {
        try {
            Class<?> cs = getNMSClass(clazz);
            return cs.getDeclaredField(fieldname).get(getInstance(clazz, instanceMethod));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets an object from instance
     *
     * @param clazz the class
     * @param instanceMethod the method
     * @return object or null
     */
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

    /**
     * Gets an instance from object
     *
     * @param from the object
     * @param instanceMethod the method
     * @return object or null
     */
    public static Object getInstance(Object from, String instanceMethod) {
        try {
            return from.getClass().getMethod(instanceMethod).invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets a field for an object
     *
     * @param obj the object
     * @param name the field name
     * @param value the value
     */
    public static void setValue(Object obj, String name, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            //Ignoring
        }
    }

    /**
     * Gets a value from object by name
     *
     * @param obj the object
     * @param name the field name
     * @return object or null
     */
    public static Object getValue(Object obj, String name) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

}
