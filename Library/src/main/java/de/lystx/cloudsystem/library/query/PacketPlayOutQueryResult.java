package de.lystx.cloudsystem.library.query;

import de.lystx.cloudsystem.library.query.QueryResult;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayOutQueryResult<T> extends Packet implements Serializable {

    private final QueryResult queryResult;
    private final T object;
    private final String message;
    private final Packet causedBy;

    public PacketPlayOutQueryResult(QueryResult queryResult, T object, String message, Packet causedBy) {
        this.queryResult = queryResult;
        this.object = object;
        this.message = message;
        this.causedBy = causedBy;
    }
}
