package de.lystx.cloudsystem.library.service.network.connection.adapter;

import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.packet.raw.PacketHandler;
import de.lystx.cloudsystem.library.service.packet.raw.PacketMethod;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter
public class PacketAdapter {

    private final List<PacketHandlerAdapter> registeredHandlers;
    private final Map<Object, List<PacketMethod>> registeredClasses;

    public PacketAdapter() {
        this.registeredHandlers = new LinkedList<>();
        this.registeredClasses = new HashMap<>();
    }



    public void registerAdapter(Object adapterHandler) {
        if (adapterHandler instanceof PacketHandlerAdapter) {
            this.registeredHandlers.add((PacketHandlerAdapter) adapterHandler);
        }
        List<PacketMethod> packetMethods = new ArrayList<>();

        for (Method m : adapterHandler.getClass().getDeclaredMethods()) {
            PacketHandler annotation = m.getAnnotation(PacketHandler.class);
            if (annotation != null) {
                Class<?> parameterType = m.getParameterTypes()[0];

                m.setAccessible(true);
                packetMethods.add(new PacketMethod(adapterHandler, m, parameterType, annotation));
            }
        }

        packetMethods.sort(Comparator.comparingInt(em -> em.getAnnotation().priority().getValue()));
        this.registeredClasses.put(adapterHandler, packetMethods);
    }

    public void unregisterAdapter(PacketHandlerAdapter adapterHandler) {
        this.registeredHandlers.remove(adapterHandler);
    }


    public void handelAdapterHandler(Packet packet) {
        try {
            registeredClasses.forEach((object, methodList) -> {
                for (PacketMethod em : methodList) {
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
        } catch (Exception e) {}
    }


}
