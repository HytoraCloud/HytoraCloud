package de.lystx.hytoracloud.driver.wrapped;

import java.io.Serializable;

public abstract class WrappedObject<I, C> implements Serializable{


    public static Class<? extends WrappedObject<?, ?>>[] WRAPPED_OBJECTS = new Class[]
            {
                    ChannelMessageObject.class,
                    CommandObject.class,
                    FileModuleObject.class,
                    IdentifiableObject.class,
                    InventoryObject.class,
                    ItemObject.class,
                    ModuleObject.class,
                    PlayerConnectionObject.class,
                    PlayerObject.class,
                    PlayerSettingsObject.class,
                    ReceiverObject.class,
                    GroupObject.class,
                    ServiceObject.class,
                    ServiceInfoObject.class,
                    TemplateObject.class,
                    SchedulerObject.class,
                    SchedulerFutureObject.class,

            };

    private static final long serialVersionUID = -4162618666025954145L;


    /**
     * The wrapped-object class (this) of the object
     *
     * @return class
     */
    abstract Class<C> getWrapperClass();

    /**
     * The interface-class of the implementation object
     *
     * @return class
     */
    abstract Class<I> getInterface();
}
