package de.lystx.hytoracloud.driver.service.util;

import de.lystx.hytoracloud.driver.elements.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.elements.interfaces.RunTaskSynchronous;
import de.lystx.hytoracloud.driver.service.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.service.util.other.ITask;
import de.lystx.hytoracloud.driver.service.util.other.ITaskListener;
import lombok.SneakyThrows;

import java.io.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Class for utiliities
 * made static because it's
 * easier to call the methods
 */
public class Utils {


    public static final String INTERNAL_RECEIVER = "InternalReceiver";
    public static final String PASTE_SERVER_URL_DOCUMENTS = "https://paste.labymod.net/documents";
    public static final String PASTE_SERVER_URL = "https://paste.labymod.net/";
    public static final String PASTE_SERVER_URL_RAW = "https://paste.labymod.net/raw/";


    public static void clearConsole() {
        try {
            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat("hh:mm:ss");
    }

    /**
     * Checks if a {@link Method} should be executed async or sync
     * and if its delayed or not and which unit to use
     *
     * @param object the object where the annotation might be placed
     * @param runnable the runnable to execute
     */
    public static void runTaskMethod(AccessibleObject object, Runnable runnable) {
        if (object.isAnnotationPresent(RunTaskSynchronous.class) || object.getClass().isAnnotationPresent(RunTaskSynchronous.class)) {
            RunTaskSynchronous runTaskSynchronous = object.getAnnotation(RunTaskSynchronous.class);
            if (runTaskSynchronous.delay() != -1 || runTaskSynchronous.unit() != TimeUnit.NANOSECONDS) {
                if (runTaskSynchronous.value()) {
                    Scheduler.getInstance().scheduleDelayedTask(runnable, runTaskSynchronous.delay());
                } else {
                    Scheduler.getInstance().scheduleDelayedTaskAsync(runnable, runTaskSynchronous.delay());
                }
            }
        } else {
            runnable.run();
        }
    }

    /**
     * Adds all objects to a string
     *
     * @param input the input to add as String
     * @return List with given objects
     */
    public static List<String> toStringList(List<?> input) {
        List<String> list = new LinkedList<>();

        for (Object o : input) {
            if (o instanceof Identifiable) {
                list.add(((Identifiable) o).getName());
                continue;
            }
            list.add(o.toString());
        }

        return list;
    }

    public static <T> void doUntilEmpty(List<T> list, Consumer<T> listConsumer, Consumer<List<T>> emptyConsumer) {
        int i = list.size();
        for (T t : list) {
            listConsumer.accept(t);
            i--;
            if (i <= 0) {
                emptyConsumer.accept(list);
            }
        }
    }

    public static void copyResource(String res, String dest, Class<?> c) throws IOException {
        InputStream src = c.getResourceAsStream(res);
        Files.copy(src, Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
    }

    @SneakyThrows
    public static void setField(Class<?> _class, Object instance, String name, Object value) {
        try {
            Field field = _class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> exceptionToList(Exception e) {
        List<String> list = new LinkedList<>();

        list.add(e.getMessage() == null ? "No message" : e.getMessage());

        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            list.add(stackTraceElement.toString());
        }

        return list;
    }

    /**
     * Checks if a class exists
     * @param name
     * @return
     */
    public static boolean existsClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SneakyThrows
    public static void createFile(File file) {
        file.createNewFile();

    }

    public static class DefaultTask<T> implements ITask<T> {

        private final List<ITaskListener<T>> iTaskListeners;
        private final T object;
        private final Consumer<ITask<T>> consumer;

        public DefaultTask(T object, Consumer<ITask<T>> consumer) {
            this.object = object;
            this.consumer = consumer;
            this.iTaskListeners = new ArrayList<>();
            this.runTask();
        }

        public void error(Throwable throwable) {
            for (ITaskListener<T> iTaskListener : this.iTaskListeners) {
                iTaskListener.onFailure(throwable);
            }
        }

        public void success(T object) {
            for (ITaskListener<T> iTaskListener : this.iTaskListeners) {
                iTaskListener.onSuccess(object);
            }
        }

        public void nulled(Class<?> nulledClass) {
            for (ITaskListener<T> iTaskListener : this.iTaskListeners) {
                iTaskListener.onNull(nulledClass);
            }
        }

        @Override
        public ITask<T> addListener(ITaskListener<T> taskListener) {
            iTaskListeners.add(taskListener);
            return this;
        }

        @Override
        public ITask<T> removeListener(ITaskListener<T> taskListener) {
            iTaskListeners.remove(taskListener);
            return this;
        }

        @Override
        public ITask<T> clearListeners() {
            iTaskListeners.clear();
            return this;
        }

        @Override
        public void runTask() {
            this.consumer.accept(this);
        }

        @Override
        public T get() {
            return this.object;
        }
    }
}
