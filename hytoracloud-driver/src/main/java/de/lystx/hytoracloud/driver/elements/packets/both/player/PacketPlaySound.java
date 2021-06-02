package de.lystx.hytoracloud.driver.elements.packets.both.player;


import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter  @AllArgsConstructor
public class PacketPlaySound extends PacketCommunication {

    private String name;
    private String sound;
    private float v1;
    private float v2;


    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        name = buf.readString();
        sound = buf.readString();
        v1 = buf.readFloat();
        v2 = buf.readFloat();
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeString(name);
        buf.writeString(sound);
        buf.writeFloat(v1);
        buf.writeFloat(v2);
    }
}
