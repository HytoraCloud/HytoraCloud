package de.lystx.cloudsystem.library.network.packet;

import de.lystx.cloudsystem.library.network.extra.util.Protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A registry for the packets. Representation of {@link Protocol}
 */
public final class PacketRegistry {

    /**
     * The list of packet classes
     */
    private final List<Class<? extends AbstractPacket>> registry;

    private static PacketRegistry instance;

    public PacketRegistry(Class<? extends AbstractPacket>... packets) {
        this.registry = new ArrayList<>(Arrays.asList(packets));
    }

    /**
     * Gets the packets registry from the protocol enum
     *
     * @return The packetRegistry
     */
    public static PacketRegistry fromProtocol() {
        PacketRegistry registry = new PacketRegistry();

        for(Protocol protocol : Protocol.values()) {
            registry.register(protocol.getPacketClass());
        }
        return registry;
    }


    public static PacketRegistry getInstance() {
        if (instance == null) {
            instance = fromProtocol();
        }
        return instance;
    }

    /**
     * Registers an packet to the registry
     *
     * @param c The class
     */
    public void register(Class<? extends AbstractPacket> c) {
        if(!contains(c)) {
            registry.add(c);
        }
    }

    /**
     * Checks if the registry contains given packet class
     *
     * @param c The class
     * @return The result
     */
    public boolean contains(Class<? extends AbstractPacket> c) {
        return registry.contains(c);
    }

    /**
     * Gets the id of given abstract packet from registry
     *
     * @param c The class
     * @return The id (-1 if class isn't registered)
     */
    public int getId(Class<? extends AbstractPacket> c) {
        if(!contains(c)) return -1;
        return registry.indexOf(c);
    }

    /**
     * Gets the packet with given id
     *
     * @param id The id of the packet
     * @return The class of the packet or null
     * @see #getId(Class)
     */
    public Class<? extends AbstractPacket> getPacket(int id) {
        if(id < 0 || id > registry.size()) return null;

        return registry.get(id);
    }

}
