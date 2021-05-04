package de.lystx.cloudsystem.library.network.extra.event;

import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import de.lystx.cloudsystem.library.network.packet.response.Response;
import de.lystx.cloudsystem.library.network.extra.util.LazySupplier;
import de.lystx.cloudsystem.library.network.extra.util.Supplyable;
import de.lystx.cloudsystem.library.service.event.Event;
import lombok.Getter;
import lombok.Setter;

/**
 * The event to call to query something from the database with {@link Queries}
 */
public class QueryEvent extends Event implements Supplyable<Response> {

    private final LazySupplier<Response> supplier;

    @Setter @Getter
    private Throwable cancelReason;

    /**
     * The packet to be either sent or simulated
     */
    @Getter
    private final AbstractPacket toQueryPacket;

    public QueryEvent(AbstractPacket toQueryPacket) {
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
