package me.lojosho.hibiscuscommons.nms.v1_21_R1.packets;

import it.unimi.dsi.fastutil.ints.IntList;
import me.lojosho.hibiscuscommons.nms.NMSPacketBuilder;
import me.lojosho.hibiscuscommons.nms.v1_21_R1.packets.wrapper.*;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NMSPackets implements NMSPacketBuilder {

    @Override
    public PacketWrapper buildEntityMovePacket(int entityId, @NotNull Location from, @NotNull Location to, boolean onGround) {
        return new EntityMoveWrapper(entityId, from, to, onGround);
    }

    @Override
    public PacketWrapper buildEntityLookAtPacket(int entityId, @NotNull Location location) {
        return new EntityLookAtWrapper(entityId, location);
    }

    @Override
    public PacketWrapper buildEntityRotatePacket(int entityId, float originalYaw, float pitch, boolean onGround) {
        return new EntityRotateWrapper(entityId, originalYaw, pitch, onGround);
    }

    @Override
    public PacketWrapper buildEntityRotateHeadPacket(int entityId, float yaw) {
        return new EntityRotateHeadWrapper(entityId, yaw);
    }

    @Override
    public PacketWrapper buildEntityMountPacket(int mountId, int[] passengerIds) {
        return new EntityMountWrapper(mountId, passengerIds);
    }

    @Override
    public PacketWrapper buildEntityLeashPacket(int leashEntity, int entityId) {
        return new EntityLeashWrapper(leashEntity, entityId);
    }

    @Override
    public PacketWrapper buildEntityTeleportPacket(int entityId, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        return new EntityTeleportWrapper(entityId, x, y, z, yaw, pitch, onGround);
    }

    @Override
    public PacketWrapper buildEntityCameraPacket(int entityId) {
        return new EntityCameraWrapper(entityId);
    }

    @Override
    public PacketWrapper buildEntitySpawnPacket(int entityId, @NotNull UUID uuid, @NotNull EntityType entityType, double x, double y, double z, float yaw, float pitch) {
        return new EntitySpawnWrapper(entityId, entityType, uuid, x, y, z, yaw, pitch);
    }

    @Override
    public PacketWrapper buildEntityMetadataPacket(int entityId, Map<Integer, Number> dataValues) {
        return new EntityMetadataWrapper(entityId, dataValues);
    }

    @Override
    public PacketWrapper buildEntityDestroyPacket(@NotNull IntList entityIds) {
        return new EntityDestroyWrapper(entityIds);
    }

    @Override
    public PacketWrapper buildEntityAttributePacket(int entityId, Attribute attribute, double value) {
        return new EntityAttributeWrapper(entityId, attribute, value);
    }

    @Override
    public PacketWrapper buildEntityEquipmentSlotUpdatePacket(int entityId, @NotNull Map<EquipmentSlot, ItemStack> equipment) {
        return new EntityEquipmentSlotUpdateWrapper(entityId, equipment);
    }

    @Override
    public PacketWrapper buildPlayerSlotUpdatePacket(@NotNull Player player, int slot) {
        return new PlayerSlotUpdateWrapper(player, slot);
    }

    @Override
    public PacketWrapper buildPlayerGamemodeChangePacket(@NotNull GameMode gameMode) {
        return new PlayerGameModeWrapper(gameMode);
    }

    @Override
    public PacketWrapper buildPlayerInfoAddPacket(@NotNull Player skinnedPlayer, int entityId, @NotNull UUID uuid, @NotNull String npcName) {
        return new PlayerInfoAddWrapper(skinnedPlayer, entityId, uuid, npcName);
    }

    @Override
    public PacketWrapper buildPlayerInfoRemovePacket(List<UUID> uuids) {
        return new PlayerInfoRemoveWrapper(uuids);
    }

    @Override
    public PacketWrapper buildPlayerScoreboardRemovePacket(Player player, String name) {
        return new PlayerScoreboardTeamRemoveWrapper(player, name);
    }

    @Override
    public PacketWrapper buildPlayerScoreboardCreatePacket(Player player, String name) {
        return new PlayerScoreboardTeamCreateWrapper(player, name);
    }

    @Override
    public PacketWrapper buildPlayerScoreboardAddPlayersPacket(Player player, String name) {
        return new PlayerScoreboardTeamAddPlayersWrapper(player, name);
    }
}
