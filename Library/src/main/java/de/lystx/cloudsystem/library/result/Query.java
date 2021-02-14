package de.lystx.cloudsystem.library.result;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;
import de.lystx.cloudsystem.library.service.util.Value;
import lombok.Getter;

import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class Query extends Thread {

    private final Value<Result> result;
    private final UUID uuid;
    private final ResultPacket resultPacket;
    private final CloudClient cloudExecutor;
    private int count;
    private Consumer<Result> consumer;
    private Consumer<Document> documentConsumer;

    public Query(ResultPacket resultPacket, CloudClient cloudExecutor) {
        this.resultPacket = resultPacket;
        this.uuid = UUID.randomUUID();
        this.result = new Value<>();
        this.cloudExecutor = cloudExecutor;
        this.count = 0;
    }


    public Query startQuery() {
        this.start();
        return this;
    }

    @Override
    public void run() {
        this.cloudExecutor.registerPacketHandler(new PacketHandlerAdapter() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof ResultPacket) {
                    ResultPacket resultPacket = (ResultPacket)packet;
                    if (uuid.equals(resultPacket.getUniqueId())) {
                        result.set(resultPacket.getResult());
                        cloudExecutor.getPacketAdapter().unregisterAdapter(this);
                    }
                }
            }
        });
        this.cloudExecutor.sendPacket(this.resultPacket.uuid(uuid));

        while (result.get() == null && count++ < 3000) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (count >= 2999) {
            Result r = new Result(uuid, new Document());
            r.setError(true);
            result.set(r);
        }
        if (this.consumer != null) {
            this.consumer.accept(result.get());
        }
        if (this.documentConsumer != null) {
            this.documentConsumer.accept(result.get().getResult());
        }
    }

    public Query onDocumentSet(Consumer<Document> consumer) {
        this.documentConsumer = consumer;
        return this;
    }

    public Query onResultSet(Consumer<Result> consumer) {
        this.consumer = consumer;
        return this;
    }
}
