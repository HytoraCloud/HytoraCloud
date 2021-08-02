package de.lystx.hytoracloud.driver.cloudservices.cloud.console.progressbar.wrapped;

import de.lystx.hytoracloud.driver.cloudservices.cloud.console.progressbar.ProgressBarBuilder;

import java.util.Iterator;


public class ProgressBarWrappedIterable<T> implements Iterable<T> {

    private Iterable<T> underlying;
    private ProgressBarBuilder pbb;

    public ProgressBarWrappedIterable(Iterable<T> underlying, ProgressBarBuilder pbb) {
        this.underlying = underlying;
        this.pbb = pbb;
    }

    public ProgressBarBuilder getProgressBarBuilder() {
        return pbb;
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<T> it = underlying.iterator();
        return new ProgressBarWrappedIterator<>(
                it,
                pbb.setInitialMax(underlying.spliterator().getExactSizeIfKnown()).build()
        );
    }
}
