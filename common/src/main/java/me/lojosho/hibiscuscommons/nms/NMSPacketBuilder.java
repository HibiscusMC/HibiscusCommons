package me.lojosho.hibiscuscommons.nms;

import it.unimi.dsi.fastutil.ints.IntList;
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

public interface NMSPacketBuilder {

    PacketWrapper buildEntityMovePacket(int entityId, @NotNull Location from, @NotNull Location to, boolean onGround);

    PacketWrapper buildEntityLookAtPacket(int entityId, @NotNull Location location);

    default PacketWrapper buildEntityRotatePacket(int entityId, Location location, boolean onGround) {
        return buildEntityRotatePacket(entityId, location.getYaw(), location.getPitch(), onGround);
    }
    PacketWrapper buildEntityRotatePacket(int entityId, float originalYaw, float pitch, boolean onGround);

    default PacketWrapper buildEntityRotateHeadPacket(int entityId, @NotNull Location location) {
        return buildEntityRotateHeadPacket(entityId, location.getYaw());
    }
    PacketWrapper buildEntityRotateHeadPacket(int entityId, float yaw);

    PacketWrapper buildEntityMountPacket(int mountId, int[] passengerIds);

    PacketWrapper buildEntityLeashPacket(int leashEntity, int entityId);

    PacketWrapper buildEntityTeleportPacket(int entityId,
                                            double x,
                                            double y,
                                            double z,
                                            float yaw,
                                            float pitch,
                                            boolean onGround);

    PacketWrapper buildEntityCameraPacket(int entityId);

    default PacketWrapper buildEntitySpawnPacket(
            int entityId,
            @NotNull UUID uuid,
            @NotNull EntityType entityType,
            @NotNull Location location
    ) {
        return buildEntitySpawnPacket(entityId, uuid, entityType, location.x(), location.y(), location.z(), location.getYaw(), location.getPitch());
    }
    PacketWrapper buildEntitySpawnPacket(
            int entityId,
            @NotNull UUID uuid,
            @NotNull EntityType entityType,
            double x,
            double y,
            double z,
            float yaw,
            float pitch
    );

    PacketWrapper buildEntityMetadataPacket(int entityId, Map<Integer, Number> dataValues);

    PacketWrapper buildEntityDestroyPacket(IntList entityIds);

    PacketWrapper buildEntityAttributePacket(
            int entityId,
            Attribute attribute,
            double value
    );

    PacketWrapper buildEntityEquipmentSlotUpdatePacket(
            int entityId,
            @NotNull Map<EquipmentSlot, ItemStack> equipment
    );

    PacketWrapper buildPlayerSlotUpdatePacket(Player player, int slot);

    PacketWrapper buildPlayerGamemodeChangePacket(@NotNull GameMode gameMode);

    PacketWrapper buildPlayerInfoAddPacket(
            @NotNull final Player skinnedPlayer,
            final int entityId,
            @NotNull final UUID uuid,
            @NotNull final String npcName
    );

    PacketWrapper buildPlayerInfoRemovePacket(List<UUID> uuids);

    PacketWrapper buildPlayerScoreboardRemovePacket(Player player, String name);
    PacketWrapper buildPlayerScoreboardCreatePacket(Player player, String name);
    PacketWrapper buildPlayerScoreboardAddPlayersPacket(Player player, String name);
}
