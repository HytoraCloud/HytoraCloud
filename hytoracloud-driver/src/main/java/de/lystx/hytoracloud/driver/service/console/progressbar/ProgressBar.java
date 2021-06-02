package de.lystx.hytoracloud.driver.service.console.progressbar;

import de.lystx.hytoracloud.driver.service.console.progressbar.wrapped.ProgressBarWrappedInputStream;
import de.lystx.hytoracloud.driver.service.console.progressbar.wrapped.ProgressBarWrappedIterable;
import de.lystx.hytoracloud.driver.service.console.progressbar.wrapped.ProgressBarWrappedIterator;
import de.lystx.hytoracloud.driver.service.console.progressbar.wrapped.ProgressBarWrappedSpliterator;

import java.io.InputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.BaseStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


import static de.lystx.hytoracloud.driver.service.console.progressbar.Util.createConsoleConsumer;

public class ProgressBar implements AutoCloseable {

    private ProgressState progress;
    private ProgressUpdateAction action;
    private ScheduledFuture<Void> scheduledTask;


    public ProgressBar(String task, long initialMax) {
        this(task, initialMax, 1000, System.err, ProgressBarStyle.COLORFUL_UNICODE_BLOCK, "", 1, false, null, ChronoUnit.SECONDS, 0L, Duration.ZERO);
    }


    public ProgressBar(
            String task,
            long initialMax,
            int updateIntervalMillis,
            PrintStream os,
            ProgressBarStyle style,
            String unitName,
            long unitSize,
            boolean showSpeed,
            DecimalFormat speedFormat,
            ChronoUnit speedUnit,
            long processed,
            Duration elapsed
    ) {
        this(task, initialMax, updateIntervalMillis, processed, elapsed,
                new DefaultProgressBarRenderer(style, unitName, unitSize, showSpeed, speedFormat, speedUnit),
                Util.createConsoleConsumer(os)
        );
    }



    public ProgressBar(
            String task,
            long initialMax,
            int updateIntervalMillis,
            long processed,
            Duration elapsed,
            ProgressBarRenderer renderer,
            ProgressBarConsumer consumer
    ) {
        this.progress = new ProgressState(task, initialMax, processed, elapsed);
        this.action = new ProgressUpdateAction(progress, renderer, consumer);
        scheduledTask = (ScheduledFuture<Void>) Util.executor.scheduleAtFixedRate(
                action, 0, updateIntervalMillis, TimeUnit.MILLISECONDS
        );
    }


    public ProgressBar stepBy(long n) {
        progress.stepBy(n);
        return this;
    }


    public ProgressBar stepTo(long n) {
        progress.stepTo(n);
        return this;
    }

    public ProgressBar step() {
        progress.stepBy(1);
        return this;
    }


    public ProgressBar maxHint(long n) {
        if (n < 0)
            progress.setAsIndefinite();
        else {
            progress.setAsDefinite();
            progress.maxHint(n);
        }
        return this;
    }


    public ProgressBar pause() {
        progress.pause();
        return this;
    }


    public ProgressBar resume() {
        progress.resume();
        return this;
    }


    @Override
    public void close() {
        scheduledTask.cancel(false);
        progress.kill();
        try {
            Util.executor.schedule(action, 0, TimeUnit.NANOSECONDS).get();
        } catch (InterruptedException | ExecutionException e) { }
    }

    public ProgressBar setExtraMessage(String msg) {
        progress.setExtraMessage(msg);
        return this;
    }


    public long getCurrent() {
        return progress.getCurrent();
    }


    public long getMax() {
        return progress.getMax();
    }

    public String getTaskName() {
        return progress.getTaskName();
    }


    public String getExtraMessage() {
        return progress.getExtraMessage();
    }


    public static <T> Iterator<T> wrap(Iterator<T> it, String task) {
        return wrap(it,
                new ProgressBarBuilder().setTaskName(task).setInitialMax(-1)
        );
    }

    public static <T> Iterator<T> wrap(Iterator<T> it, ProgressBarBuilder pbb) {
        return new ProgressBarWrappedIterator<>(it, pbb.build());
    }

    public static <T> Iterable<T> wrap(Iterable<T> ts, String task) {
        return wrap(ts, new ProgressBarBuilder().setTaskName(task));
    }


    public static <T> Iterable<T> wrap(Iterable<T> ts, ProgressBarBuilder pbb) {
        long size = ts.spliterator().getExactSizeIfKnown();
        if (size != -1)
            pbb.setInitialMax(size);
        return new ProgressBarWrappedIterable<>(ts, pbb);
    }


    public static InputStream wrap(InputStream is, String task) {
        ProgressBarBuilder pbb = new ProgressBarBuilder().setTaskName(task).setInitialMax(Util.getInputStreamSize(is));
        return wrap(is, pbb);
    }

    public static InputStream wrap(InputStream is, ProgressBarBuilder pbb) {
        long size = Util.getInputStreamSize(is);
        if (size != -1)
            pbb.setInitialMax(size);
        return new ProgressBarWrappedInputStream(is, pbb.build());
    }

    public static <T> Spliterator<T> wrap(Spliterator<T> sp, String task) {
        ProgressBarBuilder pbb = new ProgressBarBuilder().setTaskName(task);
        return wrap(sp, pbb);
    }

    public static <T> Spliterator<T> wrap(Spliterator<T> sp, ProgressBarBuilder pbb) {
        long size = sp.getExactSizeIfKnown();
        if (size != -1)
            pbb.setInitialMax(size);
        return new ProgressBarWrappedSpliterator<>(sp, pbb.build());
    }

    public static <T, S extends BaseStream<T, S>> Stream<T> wrap(S stream, String task) {
        ProgressBarBuilder pbb = new ProgressBarBuilder().setTaskName(task);
        return wrap(stream, pbb);
    }

    public static <T, S extends BaseStream<T, S>> Stream<T> wrap(S stream, ProgressBarBuilder pbb) {
        Spliterator<T> sp = wrap(stream.spliterator(), pbb);
        return StreamSupport.stream(sp, stream.isParallel());
    }

    public static <T> Stream<T> wrap(T[] array, String task) {
        ProgressBarBuilder pbb = new ProgressBarBuilder().setTaskName(task).setInitialMax(array.length);
        return wrap(array, pbb);
    }

    public static <T> Stream<T> wrap(T[] array, ProgressBarBuilder pbb) {
        return wrap(Arrays.stream(array), pbb);
    }

}
