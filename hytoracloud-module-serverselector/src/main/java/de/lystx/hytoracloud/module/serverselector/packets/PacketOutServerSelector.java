package de.lystx.hytoracloud.module.serverselector.packets;

import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.other.SerializableDocument;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.npc.NPCConfig;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.base.CloudSign;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @AllArgsConstructor
public class PacketOutServerSelector extends Packet implements Serializable {

    private List<CloudSign> cloudSigns;
    private VsonObject signLayOut;


    private NPCConfig npcConfig;
    private JsonBuilder npcs;


    @Override @SneakyThrows
    public void read(PacketBuffer buf) {


        int size = buf.readInt();

        this.cloudSigns = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            this.cloudSigns.add(CloudSign.readFromBuf(buf));
        }

        this.signLayOut = new VsonObject(buf.readString());
        this.npcs = new JsonBuilder(buf.readString());


        int inventoryRows = buf.readInt();
        String title = buf.readString();
        boolean corners = buf.readBoolean();
        String message = buf.readString();
        String itemName = buf.readString();

        int loreSize = buf.readInt();
        List<String> lore = new ArrayList<>(loreSize);

        for (int i = 0; i < loreSize; i++) {
            lore.add(buf.readString());
        }

        String itemType = buf.readString();

        int itemSize = buf.readInt();
        List<SerializableDocument> items = new ArrayList<>(itemSize);
        for (int i = 0; i < itemSize; i++) {
            items.add(SerializableDocument.fromDocument(new JsonBuilder(buf.readString())));
        }

        this.npcConfig = new NPCConfig(inventoryRows, title, corners, message, itemName, lore, itemType, items);
    }

    @Override
    public void write(PacketBuffer buf) {


        buf.writeInt(cloudSigns.size());
        for (CloudSign cloudSign : cloudSigns) {
            cloudSign.writeToBuf(buf);
        }

        buf.writeString(signLayOut.toString(FileFormat.RAW_JSON));
        buf.writeString(npcs.toString());

        buf.writeInt(npcConfig.getInventoryRows());
        buf.writeString(npcConfig.getInventoryTitle());
        buf.writeBoolean(npcConfig.isCorners());
        buf.writeString(npcConfig.getConnectingMessage());
        buf.writeString(npcConfig.getItemName());

        buf.writeInt(npcConfig.getLore().size());
        for (String s : npcConfig.getLore()) {
            buf.writeString(s);
        }

        buf.writeString(npcConfig.getItemType());

        buf.writeInt(npcConfig.getItems().size());
        for (SerializableDocument item : npcConfig.getItems()) {
            buf.writeString(item.toString());
        }
    }



}
