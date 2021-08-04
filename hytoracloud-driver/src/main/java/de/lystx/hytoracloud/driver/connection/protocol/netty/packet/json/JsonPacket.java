package de.lystx.hytoracloud.driver.connection.protocol.netty.packet.json;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.NettyPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.other.PacketBuffer;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import lombok.SneakyThrows;

import java.io.IOException;
import java.lang.reflect.Field;

public abstract class JsonPacket extends NettyPacket {

    @Override @SneakyThrows
    public final void read(PacketBuffer buf) throws IOException {

        String json = buf.readString();
        JsonObject<?> jsonObject = JsonObject.gson(json);


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

            Object value1 = JsonDocument.GSON.fromJson(sub.getElement("value").toString(), typeClass);

            Field declaredField = getClass().getDeclaredField(name);
            declaredField.setAccessible(true);
            declaredField.set(this, value1);
        }
    }

    @Override @SneakyThrows
    public final void write(PacketBuffer buf) throws IOException {

        JsonObject<?> jsonObject = JsonObject.gson();
        for (Field declaredField : this.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);

            PacketSerializable annotation = declaredField.getAnnotation(PacketSerializable.class);
            if (annotation != null) {
                Object o = declaredField.get(this);
                JsonObject<?> sub = JsonObject.gson();
                sub.append("key", declaredField.getName());
                sub.append("value", o);
                sub.append("typeClass", o.getClass().getName());

                jsonObject.append(declaredField.getName(), sub);
            }
        }

        buf.writeString(jsonObject.toString());
    }


}
