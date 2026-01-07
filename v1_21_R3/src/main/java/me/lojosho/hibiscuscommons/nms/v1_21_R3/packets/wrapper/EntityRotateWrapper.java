package me.lojosho.hibiscuscommons.nms.v1_21_R3.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;

public class EntityRotateWrapper implements PacketWrapper {

    private final int entityId;
    private final float originalYaw;
    private final float originalPitch;
    private final boolean onGround;

    public EntityRotateWrapper(int entityId, float yaw, float pitch, boolean onGround) {
        this.entityId = entityId;
        this.originalYaw = yaw;
        this.originalPitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_ROTATE;
    }

    @Override
    public Object toNativePacket() {
        float ROTATION_FACTOR = 256.0F / 360.0F;
        byte yaw = (byte) (originalYaw * ROTATION_FACTOR);
        byte pitch = (byte) (originalPitch * ROTATION_FACTOR);
        return new ClientboundMoveEntityPacket.Rot(entityId, yaw, pitch, onGround);
    }
}
