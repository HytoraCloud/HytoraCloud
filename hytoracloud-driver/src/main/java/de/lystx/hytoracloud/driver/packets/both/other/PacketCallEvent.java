package de.lystx.hytoracloud.driver.packets.both.other;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.forwarding.ForwardingPacketBuffer;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.other.PacketBuffer;
import de.lystx.hytoracloud.driver.event.IEvent;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.IOException;

@Getter @AllArgsConstructor
public class PacketCallEvent extends ForwardingPacketBuffer {

    /**
     * If events should be parsed as json string
     */
    private static final boolean USE_JSON = false;

    /**
     * The cloud event
     */
    private IEvent iEvent;

    /**
     * Who should not receive the event
     */
    private String except;


    @Override @SneakyThrows
    public void read(PacketBuffer buffer) throws IOException {
        super.read(buffer);

        this.except = buffer.readString();
        String cl = buffer.readString();
        String ev = buffer.readString();

        JsonObject<?> jsonObject = JsonObject.gson(ev);
        Class<?> eventClass = Class.forName(cl);

        this.iEvent = (IEvent) jsonObject.getAs(eventClass);
    }


    @Override
    public void write(PacketBuffer buffer) throws IOException {
        super.write(buffer);

        JsonObject<?> jsonObject = JsonObject.gson().append(iEvent);

        buffer.writeString(except);
        buffer.writeString(iEvent.getClass().getName());
        buffer.writeString(jsonObject.toString());
    }

}
