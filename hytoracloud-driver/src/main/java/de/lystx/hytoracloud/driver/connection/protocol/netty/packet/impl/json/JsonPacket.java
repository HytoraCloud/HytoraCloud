package de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json;

import com.google.gson.JsonElement;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.NettyPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.other.PacketBuffer;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import lombok.SneakyThrows;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public abstract class JsonPacket extends NettyPacket {

    @Override @SneakyThrows
    public final void read(PacketBuffer buf) throws IOException {

        String json = buf.readString();
        JsonObject<?> jsonObject = JsonObject.gson(json);

        for (Class<?> aClass : loadAllSubClasses(this.getClass())) {
            for (String name : jsonObject.keySet()) {
                JsonObject<?> sub = jsonObject.getObject(name);

                String cl = sub.getString("typeClass");

                if (cl.equalsIgnoreCase("int")) {
                    cl = "java.lang.Integer";
                } else if (cl.equalsIgnoreCase("boolean")) {
                    cl = "java.lang.Boolean";
                } else if (cl.equalsIgnoreCase("double")) {
                    cl = "java.lang.Double";
                } else if (cl.equalsIgnoreCase("short")) {
                    cl = "java.lang.Short";
                } else if (cl.equalsIgnoreCase("float")) {
                    cl = "java.lang.Float";
                } else if (cl.equalsIgnoreCase("long")) {
                    cl = "java.lang.Long";
                } else if (cl.equalsIgnoreCase("byte")) {
                    cl = "java.lang.Byte";
                }

                Class<?> typeClass = Class.forName(cl);

                Object value1;

                if (sub.has("generic") && typeClass.equals(List.class)) {
                    value1 = new LinkedList<>();
                    if (sub.has("wrapperClass")) {
                        Class<?> wrapperClass = Class.forName(sub.getString("wrapperClass"));

                        for (JsonElement value : sub.getElement("value").getAsJsonArray()) {
                            ((List)value1).add(JsonDocument.GSON.fromJson(value, wrapperClass));
                        }
                    }
                } else {
                    value1 = JsonDocument.GSON.fromJson(sub.getElement("value").toString(), typeClass);
                }

                try {
                    Field declaredField = aClass.getDeclaredField(name);
                    declaredField.setAccessible(true);
                    declaredField.set(this, value1);
                } catch (NoSuchFieldException e) {

                }
            }
        }
    }


    @Override @SneakyThrows
    public final void write(PacketBuffer buf) throws IOException {

        JsonObject<?> jsonObject = JsonObject.gson();
        for (Class<?> subClass : loadAllSubClasses(this.getClass())) {
            for (Field declaredField : subClass.getDeclaredFields()) {
                declaredField.setAccessible(true);

                PacketSerializable annotation = declaredField.getAnnotation(PacketSerializable.class);
                if (annotation != null) {
                    Object o = declaredField.get(this);
                    JsonObject<?> sub = JsonObject.gson();
                    sub.append("key", declaredField.getName());
                    sub.append("value", o);

                    if (o instanceof List && annotation.value() != Class.class) {
                        sub.append("typeClass", List.class.getName());
                        sub.append("generic", annotation.value().getName());
                        if (!((List<?>) o).isEmpty()) {
                            sub.append("wrapperClass", ((List<?>) o).get(0).getClass().getName());
                        }
                    } else {
                        sub.append("typeClass", annotation.value() == Class.class ? o.getClass().getName() : annotation.value().getName());
                    }

                    jsonObject.append(declaredField.getName(), sub);
                }
            }
        }

        buf.writeString(jsonObject.toString());
    }

    /**
     * Loads all subclasses (extended classes)
     * of another class
     *
     * @param clazz the start-class
     * @return set of classes
     */
    private List<Class<?>> loadAllSubClasses(Class<?> clazz) {
        List<Class<?>> res = new ArrayList<>();

        do {
            res.add(clazz);

            // First, add all the interfaces implemented by this class
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length > 0) {
                res.addAll(Arrays.asList(interfaces));

                for (Class<?> interfaze : interfaces) {
                    res.addAll(loadAllSubClasses(interfaze));
                }
            }

            // Add the super class
            Class<?> superClass = clazz.getSuperclass();

            // Interfaces does not have java,lang.Object as superclass, they have null, so break the cycle and return
            if (superClass == null) {
                break;
            }

            // Now inspect the superclass
            clazz = superClass;
        } while (!"java.lang.Object".equals(clazz.getCanonicalName()));

        res.remove(IPacket.class);
        res.remove(NettyPacket.class);
        res.remove(JsonPacket.class);
        return res;
    }

}
