package de.lystx.hytoracloud.driver.wrapped;

import de.lystx.hytoracloud.driver.utils.interfaces.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class IdentifiableObject extends WrappedObject<Identifiable, IdentifiableObject> implements Identifiable {

    private static final long serialVersionUID = -5773550130737529582L;
    private String name;
    private UUID uniqueId;

    public IdentifiableObject(UUID uniqueId) {
        this(null, uniqueId);
    }

    public IdentifiableObject(String name) {
        this(name, UUID.randomUUID());
    }

    @Override
    Class<IdentifiableObject> getWrapperClass() {
        return IdentifiableObject.class;
    }

    @Override
    Class<Identifiable> getInterface() {
        return Identifiable.class;
    }
}
