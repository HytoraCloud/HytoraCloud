package de.lystx.cloudsystem.library.service.throwable;

import de.lystx.cloudsystem.library.service.util.Handler;

import java.lang.reflect.Method;

public class Try {

    public static void doCatch(Method method, Runnable runnable) {
        TryCatch tryCatch = method.getAnnotation(TryCatch.class);
        if (tryCatch != null) {
            try {
                runnable.run();
            } catch (Exception e) {
                Class<? extends Handler<Throwable>> hand = (Class<? extends Handler<Throwable>>) tryCatch.handler();
                try {
                    Handler<Throwable> handler = hand.newInstance();
                    handler.handle(e);
                } catch (InstantiationException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
