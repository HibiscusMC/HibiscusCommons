package me.lojosho.hibiscuscommons.nms.v1_21_R4.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class EntityCameraWrapper implements PacketWrapper {

    private static final Entity fakeNmsEntity = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, MinecraftServer.getServer().overworld());

    private final int entityId;

    public EntityCameraWrapper(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_CAMERA;
    }

    @Override
    public Object toNativePacket() {
        fakeNmsEntity.setId(entityId);

        return new ClientboundSetCameraPacket(fakeNmsEntity);
    }
}
