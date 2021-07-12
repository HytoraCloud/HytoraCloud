package net.hytora.networking.elements.component;

import java.io.Serializable;

public interface ComponentObject<V> extends Serializable {

    void write(Component component);

    V read(Component component);

}
