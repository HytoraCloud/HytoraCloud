package de.lystx.hytoracloud.networking.events;

import de.lystx.hytoracloud.driver.service.event.CloudEvent;
import de.lystx.hytoracloud.networking.provided.supplier.LazySupplier;
import de.lystx.hytoracloud.networking.provided.supplier.Supplyable;
import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import de.lystx.hytoracloud.networking.packet.impl.response.Response;
import lombok.Getter;
import lombok.Setter;

/**
 * The event to call to query something from the database with {@link Queries}
 */
public class CloudPacketQueryEvent extends CloudEvent implements Supplyable<Response> {

    private final LazySupplier<Response> supplier;

    @Setter @Getter
    private Throwable cancelReason;

    /**
     * The packet to be either sent or simulated
     */
    @Getter
    private final AbstractPacket toQueryPacket;

    public CloudPacketQueryEvent(AbstractPacket toQueryPacket) {
        this.toQueryPacket = toQueryPacket;
        this.supplier = new LazySupplier<>();
    }

    @Override
    public LazySupplier<Response> getSupplier() {
        return supplier;
    }

    @Override
    public void accept(Response response) {
        if(!supplier.isEmpty()) return;
        supplier.accept(response);
    }

}
