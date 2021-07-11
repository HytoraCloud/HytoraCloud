
package de.lystx.hytoracloud.driver.service.managing.event.service;


import de.lystx.hytoracloud.driver.service.managing.event.handler.Event;
import de.lystx.hytoracloud.driver.service.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.service.managing.event.base.EventMethod;
import de.lystx.hytoracloud.driver.service.managing.event.handler.EventListener;
import de.lystx.hytoracloud.driver.service.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.global.main.ICloudService;
import de.lystx.hytoracloud.driver.service.global.main.ICloudServiceInfo;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter
@ICloudServiceInfo(
        name = "EventService",
        type = CloudServiceType.MANAGING,
        description = {
                "This class is used to call Events all over a process",
                "You can cancel events and register Events all in this Service"
        },
        version = 1.3
)
public class DefaultEventService implements ICloudService, IEventService {

    private final Map<Object, List<EventMethod<Event>>> registeredClasses;

    public DefaultEventService() {
        this.registeredClasses = new HashMap<>();
    }

    /**
     * Registers an EventClass
     *
     * @param listener the listener to be registered
     */
    public void registerEvent(EventListener listener) {
        List<EventMethod<Event>> eventMethods = new ArrayList<>();

        for (Method m : listener.getClass().getDeclaredMethods()) {
            Event annotation = m.getAnnotation(Event.class);

            if (annotation != null) {
                Class<?> parameterType = m.getParameterTypes()[0];

                m.setAccessible(true);
                eventMethods.add(new EventMethod<>(listener, m, parameterType, annotation));
            }
        }

        eventMethods.sort(Comparator.comparingInt(em -> em.getAnnotation().value().getValue()));
        registeredClasses.put(listener, eventMethods);
    }

    /**
     * Unregisters a class
     *
     * @param listener the listener
     */
    @Override
    public void unregister(EventListener listener) {
        registeredClasses.remove(listener);
    }

    /**
     * Calls an event
     * @param cloudEvent
     * @return if cancelled
     */
    public boolean callEvent(CloudEvent cloudEvent) {
        try {
            registeredClasses.forEach((object, methodList) -> {
                for (EventMethod<Event> em : methodList) {
                    if (em.getAClass().equals(cloudEvent.getClass())) {
                        try {
                            em.getMethod().invoke(em.getListener(), cloudEvent);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return cloudEvent.isCancelled();
        } catch (Exception e) {
            return false;
        }
    }

}
