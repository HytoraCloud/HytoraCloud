package de.lystx.hytoracloud.driver.commons.implementations;

public abstract class WrappedObject<I, C> {

    abstract Class<C> getWrapperClass();

    abstract Class<I> getInterface();
}
