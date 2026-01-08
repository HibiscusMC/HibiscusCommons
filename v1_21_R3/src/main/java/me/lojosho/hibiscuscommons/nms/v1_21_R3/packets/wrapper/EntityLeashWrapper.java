package me.lojosho.hibiscuscommons.nms.v1_21_R3.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class EntityLeashWrapper implements PacketWrapper {

    private final int leashEntity;
    private final int entityId;

    public EntityLeashWrapper(int leashEntity, int entityId) {
        this.leashEntity = leashEntity;
        this.entityId = entityId;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_LEASH;
    }

    @Override
    public Object toNativePacket() {
        ServerLevel level = MinecraftServer.getServer().overworld();
        Entity entity1 = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, level);
        Entity entity2 = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, level);
        entity1.setId(leashEntity);
        entity2.setId(entityId);

        return new ClientboundSetEntityLinkPacket(entity1, entity2);
    }
}
