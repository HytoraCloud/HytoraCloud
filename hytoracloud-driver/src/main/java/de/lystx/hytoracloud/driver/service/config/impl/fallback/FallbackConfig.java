package de.lystx.hytoracloud.driver.service.config.impl.fallback;

import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @AllArgsConstructor
public class FallbackConfig implements ThunderObject {

    /**
     * The default fallback
     */
    private Fallback defaultFallback;

    /**
     * All the other fallbacks
     */
    private List<Fallback> fallbacks;


    /**
     * Returns Fallback for group
     *
     * @param groupName the name of the group
     * @return fallback or null if not found
     */
    public Fallback getFallback(String groupName) {
        return this.fallbacks.stream().filter(fallback -> fallback.getGroupName().equalsIgnoreCase(groupName)).findFirst().orElse(null);
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeThunderObject(defaultFallback);
        buf.writeInt(fallbacks.size());
        for (Fallback fallback : fallbacks) {
            buf.writeThunderObject(fallback);
        }
    }

    @Override
    public void read(PacketBuffer buf) {
        defaultFallback = buf.readThunderObject(Fallback.class);
        int size = buf.readInt();
        fallbacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            fallbacks.add(buf.readThunderObject(Fallback.class));
        }
    }
}
