
package de.lystx.hytoracloud.driver.service.event;


import de.lystx.hytoracloud.driver.service.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.main.ICloudService;
import de.lystx.hytoracloud.driver.service.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.util.other.ObjectMethod;
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

    private final Map<Object, List<ObjectMethod<CloudEventHandler>>> registeredClasses;

    public DefaultEventService() {
        this.registeredClasses = new HashMap<>();
    }

    /**
     * Registers an EventClass
     *
     * @param o the object to be registered
     */
    public void registerEvent(Object o) {
        List<ObjectMethod<CloudEventHandler>> eventMethods = new ArrayList<>();

        for (Method m : o.getClass().getDeclaredMethods()) {
            CloudEventHandler annotation = m.getAnnotation(CloudEventHandler.class);

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
     * @param cloudEvent
     * @return if cancelled
     */
    public boolean callEvent(CloudEvent cloudEvent) {
        try {
            registeredClasses.forEach((object, methodList) -> {
                for (ObjectMethod<CloudEventHandler> em : methodList) {
                    if (em.getAClass().equals(cloudEvent.getClass())) {
                        try {
                            em.getMethod().invoke(em.getInstance(), cloudEvent);
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
