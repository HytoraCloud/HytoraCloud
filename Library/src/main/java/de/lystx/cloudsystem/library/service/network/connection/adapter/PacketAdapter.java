package de.lystx.cloudsystem.library.service.network.connection.adapter;

import de.lystx.cloudsystem.library.elements.packets.CustomPacket;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.cloudsystem.library.service.util.ObjectMethod;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter
public class PacketAdapter {

    private final List<PacketHandlerAdapter> registeredHandlers;
    private final Map<Object, List<ObjectMethod<PacketHandler>>> registeredClasses;

    public PacketAdapter() {
        this.registeredHandlers = new LinkedList<>();
        this.registeredClasses = new HashMap<>();
    }

    /**
     * Registers adapter
     * @param adapterHandler
     */
    public void registerAdapter(Object adapterHandler) {
        if (adapterHandler instanceof PacketHandlerAdapter) {
            this.registeredHandlers.add((PacketHandlerAdapter) adapterHandler);
        }
        List<ObjectMethod<PacketHandler>> packetMethods = new ArrayList<>();

        for (Method m : adapterHandler.getClass().getDeclaredMethods()) {
            PacketHandler annotation = m.getAnnotation(PacketHandler.class);
            if (annotation != null) {
                Class<?> parameterType = m.getParameterTypes()[0];

                m.setAccessible(true);
                packetMethods.add(new ObjectMethod<>(adapterHandler, m, parameterType, annotation));
            }
        }

        packetMethods.sort(Comparator.comparingInt(em -> em.getAnnotation().value().getValue()));
        this.registeredClasses.put(adapterHandler, packetMethods);
    }

    /**
     * Unregisters adapter
     * @param adapterHandler
     */
    public void unregisterAdapter(Object adapterHandler) {
        if (adapterHandler instanceof PacketHandlerAdapter) {
            this.registeredHandlers.remove(adapterHandler);
        }
        registeredClasses.remove(adapterHandler);
    }


    /**
     * Handles packet through all adapters
     * @param packet
     */
    public void handelAdapterHandler(Packet packet) {
        try {
            registeredClasses.forEach((object, methodList) -> {
                for (ObjectMethod<PacketHandler> em : methodList) {
                    PacketHandler packetHandler = em.getAnnotation();
                    if (packet instanceof CustomPacket && !packetHandler.transformTo().equals(Packet.class)) {
                        Packet examplePacket = packet.getObject("packet", packetHandler.transformTo());
                        this.handelAdapterHandler(examplePacket);
                        continue;
                    }
                    if (em.getEvent().equals(packet.getClass())) {
                        try {
                            em.getMethod().invoke(em.getInstance(), packet);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            for (PacketHandlerAdapter adapter : this.registeredHandlers) {
                adapter.handle(packet);
            }
        } catch (Exception e) {
            //Ignoring...
        }
    }


}
