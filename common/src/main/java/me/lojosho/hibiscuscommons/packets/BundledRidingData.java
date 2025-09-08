package me.lojosho.hibiscuscommons.packets;

import me.lojosho.hibiscuscommons.nms.NMSHandlers;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BundledRidingData {

    private final LinkedHashMap<Integer, List<Integer>> QUEUED_PACKETS = new LinkedHashMap<>();

    public BundledRidingData() {

    }

    public void add(int owner, int passenger) {
        add(owner, List.of(passenger));
    }

    public void add(int owner, List<Integer> passengers) {
        QUEUED_PACKETS.put(owner, passengers);
    }

    public Map<Integer, List<Integer>> getQueued() {
        return QUEUED_PACKETS;
    }

    /**
     * To be sent all at once to the players specified.
     */
    public void send(List<Player> sendTo) {
        NMSHandlers.getHandler().getPacketHandler().sendBundledRidingPacket(this, sendTo);
    }

}
