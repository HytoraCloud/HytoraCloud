package de.lystx.hytoracloud.networking.provided.objects;

import de.lystx.hytoracloud.networking.packet.PacketBuffer;

import java.io.IOException;


public interface NetworkObject {

    /**
     * Called when the object is getting written
     *
     * @param buf the buffer
     * @throws IOException if something goes wrong
     */
    void write(PacketBuffer buf) throws IOException;

    /**
     * Called when the object is getting read
     *
     * @param buf the buffer
     * @throws IOException if something goes wrong
     */
    void read(PacketBuffer buf) throws IOException;



    static NetworkObject empty() {
        return new NetworkObject() {
            @Override
            public void write(PacketBuffer buf) throws IOException {

            }

            @Override
            public void read(PacketBuffer buf) throws IOException {

            }
        };
    }
}
