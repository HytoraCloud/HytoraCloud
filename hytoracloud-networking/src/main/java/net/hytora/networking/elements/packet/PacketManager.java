package net.hytora.networking.elements.packet;

import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.connection.client.HytoraClient;
import net.hytora.networking.connection.server.HytoraServer;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.component.Component;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.*;

@Getter
public class PacketManager {

    /**
     * The connection
     */
    private final HytoraConnection connection;

    /**
     * The packetHandlers
     */
    private final List<PacketHandler> packetHandlers;

    public PacketManager(HytoraConnection connection) {
        this.connection = connection;
        this.packetHandlers = new ArrayList<>();

        if (connection instanceof HytoraServer) {
            HytoraServer hytoraServer = (HytoraServer)connection;

            hytoraServer.registerChannelHandler("_packets", serverComponent -> this.handleComponent(serverComponent.getComponent()));

        } else {
            HytoraClient hytoraClient = (HytoraClient) connection;

            hytoraClient.registerChannelHandler("_packets", clientComponent -> this.handleComponent(clientComponent.getComponent()));
        }
    }

    /**
     * Handles the component to check if its
     * a packet and then handles the built packet
     *
     * @param component the component
     */
    @SneakyThrows
    private void handleComponent(Component component) {

        if (component.has("_class")) {

            String aClass = component.get("_class");
            Class<? extends HytoraPacket> packetClass = (Class<? extends HytoraPacket>) Class.forName(aClass);
            long ms = component.get("_ms");
            UUID uniqueId = component.get("_uuid");

            //Removing unnecessary values the people dont need
            component.remove("_class");
            component.remove("_ms");
            component.remove("_uuid");

            HytoraPacket hytoraPacket;

            try {
                hytoraPacket = packetClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                hytoraPacket = getInstance(packetClass);
            }

            if (hytoraPacket == null) {
                System.out.println("[Packets] A packet could neither be constructed through NoArgsConstructor or through the PacketManager#getInstance(Class) Method!");
                System.out.println("[Packets] The failed class : " + packetClass.getName());
                System.out.println("[Packets] The packet UUID : " + uniqueId);
                System.out.println("[Packets] Query MS : " + ms);
                return;
            }

            hytoraPacket.setPacketUUID(uniqueId);
            hytoraPacket.setTime(System.currentTimeMillis() - ms);
            hytoraPacket.setConnection(this.connection);
            hytoraPacket.read(component);

            for (PacketHandler packetHandler : new ArrayList<>(this.packetHandlers)) {
                hytoraPacket.handle(connection);
                packetHandler.handle(hytoraPacket);
            }
        }
    }


    /**
     * Creates an Object from scratch
     *
     * @param tClass the object class
     */
    public static <T> T getInstance(Class<T> tClass) {
        try {
            Constructor<?> constructor;

            try {
                List<Constructor<?>> constructors = Arrays.asList(tClass.getDeclaredConstructors());

                constructors.sort(Comparator.comparingInt(Constructor::getParameterCount));

                constructor = constructors.get(constructors.size() - 1);
            } catch (Exception e) {
                constructor = null;
            }


            //Iterates through all Constructors to create a new Instance of the Object
            //And to set all values to null, -1 or false
            T object = null;
            if (constructor != null) {
                Object[] args = new Object[constructor.getParameters().length];
                for (int i = 0; i < constructor.getParameterTypes().length; i++) {
                    final Class<?> parameterType = constructor.getParameterTypes()[i];
                    if (Number.class.isAssignableFrom(parameterType)) {
                        args[i] = -1;
                    } else if (parameterType.equals(boolean.class) || parameterType.equals(Boolean.class)) {
                        args[i] = false;
                    } else if (parameterType.equals(int.class) || parameterType.equals(double.class) || parameterType.equals(short.class) || parameterType.equals(long.class) || parameterType.equals(float.class) || parameterType.equals(byte.class)) {
                        args[i] = -1;
                    } else if (parameterType.equals(Integer.class) || parameterType.equals(Double.class) || parameterType.equals(Short.class) || parameterType.equals(Long.class) || parameterType.equals(Float.class) || parameterType.equals(Byte.class)) {
                        args[i] = -1;
                    } else {
                        args[i] = null;
                    }
                }
                object = (T) constructor.newInstance(args);
            }

            if (object == null) {
                object = tClass.newInstance();
            }

            return object;
        } catch (Exception e) {
            return null;
        }
    }
}
