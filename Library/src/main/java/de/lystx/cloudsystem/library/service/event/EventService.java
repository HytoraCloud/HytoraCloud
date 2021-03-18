
package de.lystx.cloudsystem.library.service.event;


import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.util.ObjectMethod;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter
public class EventService extends CloudService {

    private final Map<Object, List<ObjectMethod<SubscribeEvent>>> registeredClasses;

    public EventService(CloudLibrary cloudLibrary, String name, CloudServiceType cloudType) {
        super(cloudLibrary, name, cloudType);
        this.registeredClasses = new HashMap<>();
    }

    /**
     * Registers an EventClass
     * @param o
     */
    public void registerEvent(Object o) {
        List<ObjectMethod<SubscribeEvent>> eventMethods = new ArrayList<>();

        for (Method m : o.getClass().getDeclaredMethods()) {
            SubscribeEvent annotation = m.getAnnotation(SubscribeEvent.class);

            if (annotation != null) {
                Class<?> parameterType = m.getParameterTypes()[0];

                m.setAccessible(true);
                eventMethods.add(new ObjectMethod<>(o, m, parameterType, annotation));
            }
        }

        eventMethods.sort(Comparator.comparingInt(em -> em.getAnnotation().value().getValue()));
        registeredClasses.put(o, eventMethods);
    }

    /**
     * Unregisters a class
     * @param instance
     */
    public void unregister(Object instance) {
        registeredClasses.remove(instance);
    }

    /**
     * Calls an event
     * @param event
     * @return if cancelled
     */
    public boolean callEvent(Event event) {
        try {
            registeredClasses.forEach((object, methodList) -> {
                for (ObjectMethod<SubscribeEvent> em : methodList) {
                    if (em.getEvent().equals(event.getClass())) {
                        try {
                            em.getMethod().invoke(em.getInstance(), event);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return event.isCancelled();
        } catch (Exception e) {
            return false;
        }
    }

}
