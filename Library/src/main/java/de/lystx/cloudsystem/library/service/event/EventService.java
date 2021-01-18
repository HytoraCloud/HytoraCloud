
package de.lystx.cloudsystem.library.service.event;


import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.event.raw.EventMethod;
import de.lystx.cloudsystem.library.service.event.raw.SubscribeEvent;
import lombok.Getter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter
public class EventService extends CloudService {

    private final Map<Object, List<EventMethod>> registeredClasses;

    public EventService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.registeredClasses = new HashMap<>();
    }


    public void registerEvent(Object o) {
        List<EventMethod> eventMethods = new ArrayList<>();

        for (Method m : o.getClass().getDeclaredMethods()) {
            SubscribeEvent annotation = m.getAnnotation(SubscribeEvent.class);

            if (annotation != null) {
                Class<?> parameterType = m.getParameterTypes()[0];

                m.setAccessible(true);
                eventMethods.add(new EventMethod(o, m, parameterType, annotation));
            }
        }

        eventMethods.sort(Comparator.comparingInt(em -> em.getAnnotation().priority().getValue()));
        registeredClasses.put(o, eventMethods);
    }

    public void unregister(Object instance) {
        registeredClasses.remove(instance);
    }
    
    public boolean callEvent(Event event) {
        try {
            registeredClasses.forEach((object, methodList) -> {
                for (EventMethod em : methodList) {
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