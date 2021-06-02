package de.lystx.hytoracloud.networking.provided.supplier;

public interface Supplyable<T> {


    void accept(T t);

    LazySupplier<T> getSupplier();
}
