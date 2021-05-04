package de.lystx.cloudsystem.library.network.packet;

import de.lystx.cloudsystem.library.service.util.ObjectMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A class to execute packet events (i/o)
 */
public final class PacketAdapting {

    private static PacketAdapting instance;

    public static synchronized PacketAdapting getInstance() {
        if(instance == null) {
            instance = new PacketAdapting();
        }
        return instance;
    }

    private final Map<Object, List<ObjectMethod<PacketListener>>> registeredClasses;


    public PacketAdapting() {
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
                for (ObjectMethod<PacketListener> em : methodList) {
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

        List<ObjectMethod<PacketListener>> packetMethods = new ArrayList<>();

        for (Method m : adapterHandler.getClass().getDeclaredMethods()) {
            PacketListener annotation = m.getAnnotation(PacketListener.class);
            if (annotation != null) {
                Class<?> parameterType = m.getParameterTypes()[0];

                m.setAccessible(true);
                packetMethods.add(new ObjectMethod<>(adapterHandler, m, parameterType, annotation));
            }
        }

        packetMethods.sort(Comparator.comparingInt(em -> em.getAnnotation().value().getValue()));
        this.registeredClasses.put(adapterHandler, packetMethods);
    }


}
