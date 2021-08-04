
package de.lystx.hytoracloud.driver.event.def;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.event.*;
import de.lystx.hytoracloud.driver.event.base.HandlerMethod;
import de.lystx.hytoracloud.driver.event.ICancellable;
import de.lystx.hytoracloud.driver.event.base.NetworkHandlerListener;
import de.lystx.hytoracloud.driver.event.handle.EventHandler;
import de.lystx.hytoracloud.driver.event.handle.IListener;
import de.lystx.hytoracloud.driver.event.handle.IEventHandler;
import de.lystx.hytoracloud.driver.packets.both.other.PacketCallEvent;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter
public class DefaultEventManager implements IEventManager {

    /**
     * All cached registered classes
     */
    private final Map<Object, List<HandlerMethod<EventHandler>>> registeredClasses;
    private final Map<Class<? extends IEvent>, List<IEventHandler<?>>> eventHandlers;

    public DefaultEventManager() {
        this.registeredClasses = new HashMap<>();
        this.eventHandlers = new HashMap<>();
        this.registerListener(new NetworkHandlerListener());

    }

    @Override
    public void registerListener(IListener listener) {
        List<HandlerMethod<EventHandler>> handlerMethods = new ArrayList<>();

        for (Method m : listener.getClass().getDeclaredMethods()) {
            EventHandler annotation = m.getAnnotation(EventHandler.class);

            if (annotation != null) {
                Class<?> parameterType = m.getParameterTypes()[0];

                m.setAccessible(true);
                handlerMethods.add(new HandlerMethod<>(listener, m, parameterType, annotation));
            }
        }

        handlerMethods.sort(Comparator.comparingInt(em -> em.getAnnotation().value().getValue()));
        registeredClasses.put(listener, handlerMethods);
    }

    @Override
    public void unregisterListener(IListener listener) {
        registeredClasses.remove(listener);
    }

    @Override
    public <E extends IEvent> void registerHandler(Class<E> eventClass, IEventHandler<E> handler) {
        List<IEventHandler<?>> iEventHandlers = eventHandlers.get(eventClass);
        if (iEventHandlers == null) {
            iEventHandlers = new LinkedList<>();
        }
        iEventHandlers.add(handler);
        this.eventHandlers.put(eventClass, iEventHandlers);
    }

    @Override
    public <E extends IEvent> void unregisterHandler(Class<E> eventClass, IEventHandler<E> handler) {
        List<IEventHandler<?>> iEventHandlers = eventHandlers.get(eventClass);
        if (iEventHandlers == null) {
            iEventHandlers = new LinkedList<>();
        }
        iEventHandlers.remove(handler);
        this.eventHandlers.put(eventClass, iEventHandlers);
    }

    @Override
    public boolean callEvent(IEvent event) {


        if ((CloudDriver.getInstance().getServiceManager() == null || CloudDriver.getInstance().getServiceManager().getThisService() == null) && CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            return false;
        }

        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            if (CloudDriver.getInstance().getConnection() != null) {
                CloudDriver.getInstance().getConnection().sendPacket(new PacketCallEvent(event, CloudDriver.getInstance().getServiceManager().getThisService().getName()));
            }
        } else {
            if (CloudDriver.getInstance().getConnection() != null) {
                CloudDriver.getInstance().getConnection().sendPacket(new PacketCallEvent(event, "cloud"));
            }
        }

        for (Class<? extends IEvent> aClass : this.eventHandlers.keySet()) {
            if (event.getClass().equals(aClass)) {
                for (IEventHandler iEventHandler : this.eventHandlers.get(aClass)) {
                    iEventHandler.handle(event);
                }
            }
        }
        try {
            registeredClasses.forEach((object, methodList) -> {
                for (HandlerMethod<EventHandler> em : methodList) {
                    if (em.getAClass().equals(event.getClass())) {
                        CloudDriver.getInstance().runTask(em.getMethod(), () -> {
                            try {
                                em.getMethod().invoke(em.getListener(), event);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            });
            return event instanceof ICancellable && ((ICancellable) event).isCancelled();
        } catch (Exception e) {
            return false;
        }
    }

}
