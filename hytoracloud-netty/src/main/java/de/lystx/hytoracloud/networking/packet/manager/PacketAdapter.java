package de.lystx.hytoracloud.networking.packet.manager;

import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import de.lystx.hytoracloud.networking.provided.objects.PacketMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A class to execute packet events (i/o)
 */
public class PacketAdapter {


    private final Map<Object, List<PacketMethod<PacketListener>>> registeredClasses;


    public PacketAdapter() {
        this.registeredClasses = new HashMap<>();
    }

    /**
     * Executes given packet
     *
     * @param packet The packet
     */
    public void execute(AbstractPacket packet) {
        try {
            registeredClasses.forEach((object, methodList) -> {
                for (PacketMethod<PacketListener> em : methodList) {
                    if (em.getAClass().equals(packet.getClass())) {
                        try {
                            em.getMethod().invoke(em.getInstance(), packet);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            //Ignoring...
        }
    }

    /**
     * Unregisters event listener
     *
     * @param adapterHandler The packetAdapter classes
     */
    public void unregister(Object adapterHandler) {
        registeredClasses.remove(adapterHandler);
    }

    /**
     * Registers event listener
     *
     * @param adapterHandler The packetAdapter classes
     */
    public void register(Object adapterHandler) {

        List<PacketMethod<PacketListener>> packetMethods = new ArrayList<>();

        for (Method m : adapterHandler.getClass().getDeclaredMethods()) {
            PacketListener annotation = m.getAnnotation(PacketListener.class);
            if (annotation != null) {
                Class<?> parameterType = m.getParameterTypes()[0];

                m.setAccessible(true);
                packetMethods.add(new PacketMethod<>(adapterHandler, m, parameterType, annotation));
            }
        }

        this.registeredClasses.put(adapterHandler, packetMethods);
    }


}
