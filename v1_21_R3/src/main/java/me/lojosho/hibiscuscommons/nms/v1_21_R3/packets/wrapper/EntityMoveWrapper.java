package me.lojosho.hibiscuscommons.nms.v1_21_R3.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class EntityMoveWrapper implements PacketWrapper {

    private final int entityId;
    private final @NotNull Location from;
    private final @NotNull Location to;
    private final boolean onGround;

    public EntityMoveWrapper(int entityId, @NotNull Location from, @NotNull Location to, boolean onGround) {
        this.entityId = entityId;
        this.from = from;
        this.to = to;
        this.onGround = onGround;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_MOVE;
    }

    @Override
    public Object toNativePacket() {
        byte dx = (byte) (to.getX() -  from.getX());
        byte dy = (byte) (to.getY() - from.getY());
        byte dz = (byte) (to.getZ() - from.getZ());

        return new ClientboundMoveEntityPacket.Pos(entityId, dx, dy, dz, onGround);
    }
}
