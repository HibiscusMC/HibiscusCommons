package me.lojosho.hibiscuscommons.nms.v1_21_R1.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class EntityTeleportWrapper implements PacketWrapper {

    private static final Entity fakeNmsEntity = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, MinecraftServer.getServer().overworld());

    private final int entityId;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final boolean onGround;

    public EntityTeleportWrapper(int entityId,
                                 double x,
                                 double y,
                                 double z,
                                 float yaw,
                                 float pitch,
                                 boolean onGround) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_TELEPORT;
    }

    @Override
    public Object toNativePacket() {
        fakeNmsEntity.setId(entityId);
        fakeNmsEntity.teleportTo(x, y, z);
        return new ClientboundTeleportEntityPacket(fakeNmsEntity);
    }
}
