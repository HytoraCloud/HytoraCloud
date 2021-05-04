package de.lystx.cloudsystem.library.network.extra.util;

public interface Supplyable<T> {


    void accept(T t);

    LazySupplier<T> getSupplier();
}
