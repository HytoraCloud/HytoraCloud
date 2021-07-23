
package de.lystx.hytoracloud.driver.cloudservices.managing.event.service;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.DefaultListener;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventMarker;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.EventMethod;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventListener;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter
public class DefaultEventService implements IEventService {

    /**
     * All cached registered classes
     */
    private final Map<Object, List<EventMethod<EventMarker>>> registeredClasses;

    public DefaultEventService() {
        this.registeredClasses = new HashMap<>();
        this.registerEvent(new DefaultListener());
    }

    @Override
    public void registerEvent(EventListener listener) {
        List<EventMethod<EventMarker>> eventMethods = new ArrayList<>();

        for (Method m : listener.getClass().getDeclaredMethods()) {
            EventMarker annotation = m.getAnnotation(EventMarker.class);

            if (annotation != null) {
                Class<?> parameterType = m.getParameterTypes()[0];

                m.setAccessible(true);
                eventMethods.add(new EventMethod<>(listener, m, parameterType, annotation));
            }
        }

        eventMethods.sort(Comparator.comparingInt(em -> em.getAnnotation().value().getValue()));
        registeredClasses.put(listener, eventMethods);
    }

    @Override
    public void unregister(EventListener listener) {
        registeredClasses.remove(listener);
    }

    @Override
    public boolean callEvent(CloudEvent cloudEvent) {
        try {
            registeredClasses.forEach((object, methodList) -> {
                for (EventMethod<EventMarker> em : methodList) {
                    if (em.getAClass().equals(cloudEvent.getClass())) {
                        CloudDriver.getInstance().runTask(em.getMethod(), () -> {
                            try {
                                em.getMethod().invoke(em.getListener(), cloudEvent);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            });
            return cloudEvent.isCancelled();
        } catch (Exception e) {
            return false;
        }
    }

}
