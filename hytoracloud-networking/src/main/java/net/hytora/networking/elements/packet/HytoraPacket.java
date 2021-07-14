package net.hytora.networking.elements.packet;

import com.sun.xml.internal.ws.api.message.Packet;
import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.PacketRespond;
import net.hytora.networking.elements.packet.response.ResponseStatus;
import net.hytora.networking.elements.component.Component;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.function.Consumer;

@Getter @Setter
public abstract class HytoraPacket {

    /**
     * The connection of the packet
     */
    protected HytoraConnection connection;

    /**
     * THe time the packet took to process
     */
    protected long time;

    /**
     * The uuid of the packet
     */
    protected UUID packetUUID = UUID.randomUUID();

    /**
     * Called when the packet is written and
     * prepared to be send so all the attributes
     * of the packet can be put in the component
     *
     * @param component the component
     */
    public abstract void write(Component component);

    /**
     * Called when the packet is received and is
     * built together again so the attributes of the
     * packet can be set again from the component
     *
     * @param component the component
     */
    public abstract void read(Component component);

    /**
     * Called when the packet gets handled
     *
     * @param connection the connection it handles
     */
    public void handle(HytoraConnection connection) {

    }

    public void toReply(HytoraConnection connection, Consumer<Component> consumer) {

        long start = System.currentTimeMillis();
        this.connection = connection;

        connection.registerPacketHandler(new PacketHandler() {
            @Override
            public void handle(HytoraPacket packet) {

                if (packet instanceof PacketRespond) {
                    PacketRespond packetRespond = (PacketRespond)packet;
                    if (packet.getPacketUUID().equals(packetUUID)) {

                        if (packetRespond.getHytoraComponent() != null) {
                            consumer.accept(packetRespond.getHytoraComponent());
                        } else {
                            Component component = new Component();

                            component.append(map -> {
                                map.put("_time", (System.currentTimeMillis() - start));
                                map.put("_message", packetRespond.getMessage());
                                map.put("_status", packetRespond.getStatus().name());
                            });
                            consumer.accept(component);
                        }
                        connection.unregisterPacketHandler(this);
                    }
                }
            }
        });
        connection.sendPacket(this);
    }

    /**
     * Requests a component as reply
     *
     * @param connection the connection
     * @return component
     */
    public Component toReply(HytoraConnection connection) {
        return this.toReply(connection, 3000);
    }


    /**
     * Calls the Method
     * This will wait for the given Packet to respond
     * And if the {@link Packet} responded it will return its
     * to work with it
     *
     * @return the Response the Packet gets
     */
    public Component toReply(HytoraConnection connection, int timeOut) {

        long start = System.currentTimeMillis();
        this.connection = connection;
        Component[] response = {null};

        connection.registerPacketHandler(new PacketHandler() {
            @Override
            public void handle(HytoraPacket packet) {

                if (packet instanceof PacketRespond) {
                    PacketRespond packetRespond = (PacketRespond)packet;
                    if (packet.getPacketUUID().equals(packetUUID)) {

                        if (packetRespond.getHytoraComponent() != null) {
                            response[0] = packetRespond.getHytoraComponent();
                        } else {
                            Component hytoraComponent = new Component();


                            hytoraComponent.append(map -> {
                                map.put("_time", (System.currentTimeMillis() - start));
                                map.put("_message", packetRespond.getMessage());
                                map.put("_status", packetRespond.getStatus().name());
                            });
                            response[0] = hytoraComponent;
                        }
                        connection.unregisterPacketHandler(this);
                    }
                }
            }
        });

        connection.sendPacket(this);
        int count = 0;
        while (response[0] == null && count++ < timeOut) {
            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (count >= timeOut) {
            Component hytoraComponent = new Component();

            hytoraComponent.append(map -> {
                map.put("_time", (System.currentTimeMillis() - start));
                map.put("_message", "The request timed out");
                map.put("_status", ResponseStatus.FAILED.name());
            });
            response[0] = hytoraComponent;
        }
        return response[0];
    }

    /**
     * Respond to the packet with a {@link PacketRespond}
     *
     * @param packet  The packets to send as respond
     */
    private void reply(PacketRespond packet) {
        //Sends the packet over the connection of this Packet

        packet.setPacketUUID(this.packetUUID);
        packet.setConnection(this.connection);

        this.connection.sendPacket(packet);
    }

    /**
     * Responds to the Packet with a {@link ResponseStatus}
     *
     * @param status the status you want to respond
     */
    public void reply(ResponseStatus status) {
        this.reply(status, "No message provided");
    }

    /**
     * Responds to the packet with a {@link Component}
     *
     * @param component the component to respond with
     */
    public void reply(Component component) {
        this.reply(new PacketRespond("No message provided", ResponseStatus.FORBIDDEN, component));
    }

    /**
     * Responds to the packet with a {@link Component}
     *
     * @param consumer the consumer to handle
     */
    public void reply(Consumer<Component> consumer) {
        Component component = new Component();
        consumer.accept(component);
        this.reply(component);
    }

    /**
     * Responds to the Packet with a {@link ResponseStatus} and a message
     *
     * @param status the status you want to respond
     * @param message the message you want to respond
     */
    public void reply(ResponseStatus status, String message) {
        this.reply(new PacketRespond(message, status, null));
    }

    public void reply(ResponseStatus status, Object message) {
        this.reply(status, message.toString());
    }

}
