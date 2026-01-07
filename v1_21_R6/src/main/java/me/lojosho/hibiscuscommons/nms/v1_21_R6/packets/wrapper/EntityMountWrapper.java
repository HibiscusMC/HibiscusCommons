package me.lojosho.hibiscuscommons.nms.v1_21_R6.packets.wrapper;

import com.google.common.collect.ImmutableList;
import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.Arrays;
import java.util.List;

public class EntityMountWrapper implements PacketWrapper {
    private static final Entity fakeNmsEntity = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, MinecraftServer.getServer().overworld());

    private final int mountId;
    private final int[] passengerIds;

    public EntityMountWrapper(int mountId, int[] passengerIds) {
        this.mountId = mountId;
        this.passengerIds = passengerIds;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_MOUNT;
    }

    @Override
    public Object toNativePacket() {
        List<Entity> passengers = Arrays.stream(passengerIds).mapToObj(id -> {
            Entity passenger = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, MinecraftServer.getServer().overworld());
            passenger.setId(id);
            return passenger;
        }).toList();
        fakeNmsEntity.setId(mountId);
        fakeNmsEntity.ejectPassengers();
        fakeNmsEntity.passengers = ImmutableList.copyOf(passengers);
        return new ClientboundSetPassengersPacket(fakeNmsEntity);
    }
}
