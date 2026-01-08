package me.lojosho.hibiscuscommons.nms.v1_21_R1.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class EntityRotateHeadWrapper implements PacketWrapper {

    private static final Entity fakeNmsEntity = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, MinecraftServer.getServer().overworld());

    private final int entityId;
    private final float yaw;

    public EntityRotateHeadWrapper(int entityId, float yaw) {
        this.entityId = entityId;
        this.yaw = yaw;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_ROTATE_HEAD;
    }

    @Override
    public Object toNativePacket() {
        fakeNmsEntity.setId(entityId);
        byte headRot = (byte) (yaw * 256.0F / 360.0F);

        return new ClientboundRotateHeadPacket(fakeNmsEntity, headRot);
    }
}
