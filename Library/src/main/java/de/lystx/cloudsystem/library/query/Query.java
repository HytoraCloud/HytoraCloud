package de.lystx.cloudsystem.library.query;

import de.lystx.cloudsystem.library.elements.other.Triple;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;
import lombok.Getter;

@Getter
public class Query<S, G> {

    private final S send;
    private final Triple<QueryResult, G, String> consumer;

    public Query(CloudClient executor, S send, Triple<QueryResult, G, String> consumer, String... filter) {
        this.send = send;
        this.consumer = consumer;

        executor.registerPacketHandler(new PacketHandlerAdapter() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof PacketPlayOutQueryResult) {
                    PacketPlayOutQueryResult<G> result = (PacketPlayOutQueryResult<G>)packet;
                    for (String filter : filter) {
                        if (filter.equalsIgnoreCase(result.getCausedBy().getClass().toString())) {
                            consumer.accept(result.getQueryResult(), result.getObject(), result.getMessage());
                            executor.getAdapterHandler().unregisterAdapter(this);
                            break;
                        }
                    }
                }
            }
        });

    }

    public Query<S, G> onReady(Runnable runnable) {
        runnable.run();
        return this;
    }
}
