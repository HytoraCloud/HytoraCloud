package de.lystx.hytoracloud.driver.commons.wrapped;

import java.io.Serializable;

public abstract class WrappedObject<I, C> implements Serializable{

    private static final long serialVersionUID = -4162618666025954145L;

    abstract Class<C> getWrapperClass();

    abstract Class<I> getInterface();
}
