package me.lojosho.hibiscuscommons.nms.v1_21_R2.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;

import java.util.List;
import java.util.UUID;

public class PlayerInfoRemoveWrapper implements PacketWrapper {

    private final List<UUID> uuids;

    public PlayerInfoRemoveWrapper(List<UUID> uuids) {
        this.uuids = uuids;
    }

    @Override
    public PacketType getType() {
        return PacketType.PLAYER_INFO_REMOVE;
    }

    @Override
    public Object toNativePacket() {
        return new ClientboundPlayerInfoRemovePacket(uuids);
    }
}
