package de.lystx.hytoracloud.driver.commons.wrapped;

import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class Identification extends WrappedObject<Identifiable, Identification> implements Identifiable {

    private static final long serialVersionUID = -5773550130737529582L;
    private String name;
    private UUID uniqueId;

    public Identification(UUID uniqueId) {
        this(null, uniqueId);
    }

    public Identification(String name) {
        this(name, UUID.randomUUID());
    }

    @Override
    Class<Identification> getWrapperClass() {
        return Identification.class;
    }

    @Override
    Class<Identifiable> getInterface() {
        return Identifiable.class;
    }
}
