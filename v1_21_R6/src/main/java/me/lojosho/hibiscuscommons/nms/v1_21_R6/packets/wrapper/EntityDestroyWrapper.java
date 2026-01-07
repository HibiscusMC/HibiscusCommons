package me.lojosho.hibiscuscommons.nms.v1_21_R6.packets.wrapper;

import it.unimi.dsi.fastutil.ints.IntList;
import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;

public class EntityDestroyWrapper implements PacketWrapper {

    private final IntList entityIds;

    public EntityDestroyWrapper(IntList entityIds) {
        this.entityIds = entityIds;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_DESTROY;
    }

    @Override
    public Object toNativePacket() {
        return new ClientboundRemoveEntitiesPacket(entityIds);
    }
}
