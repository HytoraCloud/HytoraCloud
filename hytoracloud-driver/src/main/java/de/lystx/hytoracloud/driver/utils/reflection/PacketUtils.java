package de.lystx.hytoracloud.driver.utils.reflection;

/**
 * SOURCES FROM LABYMODAPI FOR 3.0
 * SPIGOT : https://www.spigotmc.org/resources/labymod-server-api.52423/
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import de.lystx.hytoracloud.driver.CloudDriver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

public class PacketUtils {

    private final String version = CloudDriver.getInstance().getBukkit().getVersion();
    private Class<?> packetClass;
    private Class<?> packetPlayOutCustomPayloadClass;
    private Constructor<?> customPayloadConstructor;
    private boolean customPayloadHasBytes;
    private Constructor<?> packetDataSerializerConstructor;
    private Method getHandleMethod;
    private Field playerConnectionField;
    private Field networkManagerField;

    public PacketUtils() {
        try {
            this.packetClass = this.getNmsClass("Packet");
            this.packetPlayOutCustomPayloadClass = this.getNmsClass("PacketPlayOutCustomPayload");
            this.networkManagerField = this.getNmsClass("PlayerConnection").getDeclaredField("networkManager");
        } catch (NoSuchFieldException | ClassNotFoundException var6) {
            var6.printStackTrace();
        }

        if (this.packetPlayOutCustomPayloadClass != null) {
            Constructor[] var1 = this.packetPlayOutCustomPayloadClass.getDeclaredConstructors();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                Constructor<?> constructors = var1[var3];
                if (constructors.getParameterTypes().length == 2 && constructors.getParameterTypes()[1] == byte[].class) {
                    this.customPayloadHasBytes = true;
                    this.customPayloadConstructor = constructors;
                } else if (constructors.getParameterTypes().length == 2 && constructors.getParameterTypes()[1].getSimpleName().equals("PacketDataSerializer")) {
                    this.customPayloadConstructor = constructors;
                }
            }

            if (!this.customPayloadHasBytes) {
                try {
                    Class<?> packetDataSerializerClass = this.getNmsClass("PacketDataSerializer");
                    this.packetDataSerializerConstructor = packetDataSerializerClass.getDeclaredConstructor(ByteBuf.class);
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            }
        }

    }

    public Object getPlayerHandle(Object player) {
        try {
            if (this.getHandleMethod == null) {
                this.getHandleMethod = player.getClass().getMethod("getHandle");
            }

            return this.getHandleMethod.invoke(player);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public Object getPlayerConnection(Object nmsPlayer) {
        try {
            if (this.playerConnectionField == null) {
                this.playerConnectionField = nmsPlayer.getClass().getField("playerConnection");
            }

            return this.playerConnectionField.get(nmsPlayer);
        } catch (NoSuchFieldException | IllegalAccessException var3) {
            var3.printStackTrace();
            return null;
        }
    }



    public byte[] getBytesToSend(String messageKey, String messageContents) {
        ByteBuf byteBuf = Unpooled.buffer();
        this.writeString(byteBuf, messageKey);
        this.writeString(byteBuf, messageContents);
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    public JsonElement cloneJson(JsonElement cloneElement) {
        if (cloneElement.toString().equalsIgnoreCase("{}")) {
            return cloneElement;
        }
        try {
            return new JsonParser().parse(cloneElement.toString());
        } catch (JsonParseException var3) {
            var3.printStackTrace();
            return null;
        }
    }


    private void writeVarIntToBuffer(ByteBuf buf, int input) {
        while((input & -128) != 0) {
            buf.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        buf.writeByte(input);
    }

    private void writeString(ByteBuf buf, String string) {
        byte[] abyte = string.getBytes(Charset.forName("UTF-8"));
        if (abyte.length > 32767) {
            throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + 32767 + ")");
        } else {
            this.writeVarIntToBuffer(buf, abyte.length);
            buf.writeBytes(abyte);
        }
    }

    public int readVarIntFromBuffer(ByteBuf buf) {
        int i = 0;
        int j = 0;

        byte b0;
        do {
            b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while((b0 & 128) == 128);

        return i;
    }

    public String readString(ByteBuf buf, int maxLength) {
        int i = this.readVarIntFromBuffer(buf);
        if (i > maxLength * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
        } else if (i < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            byte[] bytes = new byte[i];
            buf.readBytes(bytes);
            String s = new String(bytes, Charset.forName("UTF-8"));
            if (s.length() > maxLength) {
                throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
            } else {
                return s;
            }
        }
    }

   public void sendPacket(Object player, Object packet) {
        try {
            Object nmsPlayer = this.getPlayerHandle(player);
            Object playerConnection = this.getPlayerConnection(nmsPlayer);
            playerConnection.getClass().getMethod("sendPacket", this.packetClass).invoke(playerConnection, packet);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public Object getPluginMessagePacket(String channel, byte[] bytes) {
        try {
            return this.customPayloadConstructor.newInstance(channel, this.customPayloadHasBytes ? bytes : this.packetDataSerializerConstructor.newInstance(Unpooled.wrappedBuffer(bytes)));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NullPointerException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + this.version + "." + nmsClassName);
    }

    public void setField(Object targetObject, String fieldName, Object value) {
        try {
            Field field = targetObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(targetObject, value);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public Field getNetworkManagerField() {
        return this.networkManagerField;
    }
}
