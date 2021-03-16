package de.lystx.cloudsystem.library.service.server.other.process;

import de.lystx.cloudsystem.library.service.scheduler.Scheduler;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Coded by CryCodes on 15.03.2021
 * Discord: CryCodes#7342
 */

public class Threader {

    private static Threader instance;

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(createThreadFactory((thread, throwable) -> {
        if (thread != null && !thread.isInterrupted())
            thread.interrupt();
    }));


    public static Threader getInstance() {
        if (instance == null) {
            instance = new Threader();
        }
        return instance;
    }

    private static ThreadFactory createThreadFactory(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        AtomicLong atomicLong = new AtomicLong(0);
        return runnable -> {
            Thread thread = threadFactory.newThread(runnable);
            thread.setName(String
                    .format(Locale.ROOT, "PoolThread-%d",
                            atomicLong.getAndIncrement()));
            thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            thread.setDaemon(true);
            return thread;
        };
    }

    public void execute(Runnable runnable) {
        EXECUTOR_SERVICE.submit(runnable);
    }

    public void execute(Runnable runnable, long delay) {
        Scheduler.getInstance().scheduleDelayedTask(() -> EXECUTOR_SERVICE.submit(runnable), delay);
    }

    public void startProcess(String[] command, File location, Consumer<Process> processConsumer, Consumer<Throwable> throwableConsumer) {
        if (!EXECUTOR_SERVICE.isTerminated() && command != null && command.length != 0 && location.exists() && location.isDirectory()) {
            EXECUTOR_SERVICE.submit(() -> {
                try {
                    final ProcessBuilder processBuilder = new ProcessBuilder(command).directory(location);
                    final Process process = processBuilder.start();

                    if (process.isAlive()) {
                        processConsumer.accept(process);
                        if (throwableConsumer != null) throwableConsumer.accept(null);

                        return;
                    }
                    if (throwableConsumer != null)  throwableConsumer.accept(new IllegalStateException("Process terminated itself or couldn't be started"));
                } catch (IOException exception) {
                    if (throwableConsumer != null)  throwableConsumer.accept(exception);
                }
            });
            return;
        }
        if (throwableConsumer != null)  throwableConsumer.accept(new IllegalStateException("ThreadPool is dead or command is corrupted or Locations is a Directory!"));
    }

    public void startProcess(String[] command, File location, Consumer<Process> processConsumer) {
        this.startProcess(command, location, processConsumer, null);
    }
}

