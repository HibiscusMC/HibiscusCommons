package me.lojosho.hibiscuscommons.util.packets;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.lojosho.hibiscuscommons.nms.NMSHandlers;
import me.lojosho.hibiscuscommons.util.MessagesUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PacketManager {

    public static void sendEntitySpawnPacket(
            final @NotNull Location location,
            final int entityId,
            final EntityType entityType,
            final UUID uuid,
            final @NotNull List<Player> sendTo
    ) {
        NMSHandlers.getHandler().getPacketHandler().sendSpawnEntityPacket(entityId, uuid, entityType, location, sendTo);
    }

    public static void gamemodeChangePacket(
            Player player,
            GameMode gamemode
    ) {
        NMSHandlers.getHandler().getPacketHandler().sendGamemodeChange(player, gamemode);
    }

    public static void ridingMountPacket(
            int mountId,
            int passengerId,
            @NotNull List<Player> sendTo
    ) {
        NMSHandlers.getHandler().getPacketHandler().sendMountPacket(mountId, new int[]{passengerId}, sendTo);
    }

    public static void sendRotateHeadPacket(
            int entityId,
            @NotNull Location location,
            @NotNull List<Player> sendTo
    ) {
        NMSHandlers.getHandler().getPacketHandler().sendRotateHeadPacket(entityId, location, sendTo);
    }

    public static void sendRotationPacket(
            int entityId,
            @NotNull Location location,
            boolean onGround,
            @NotNull List<Player> sendTo
    ) {
        NMSHandlers.getHandler().getPacketHandler().sendRotationPacket(entityId, location, onGround, sendTo);
    }

    public static void sendRidingPacket(
            final int mountId,
            final int passengerId,
            final @NotNull List<Player> sendTo
    ) {
        sendRidingPacket(mountId, new int[] {passengerId}, sendTo);
    }

    public static void sendRidingPacket(
            final int mountId,
            final int[] passengerIds,
            final @NotNull List<Player> sendTo
    ) {
        NMSHandlers.getHandler().getPacketHandler().sendMountPacket(mountId, passengerIds, sendTo);
    }

    /**
     * Destroys an entity from a player
     * @param entityId The entity to delete for a player
     * @param sendTo The players the packet should be sent to
     */
    public static void sendEntityDestroyPacket(final int entityId, @NotNull List<Player> sendTo) {
        NMSHandlers.getHandler().getPacketHandler().sendEntityDestroyPacket(IntList.of(entityId), sendTo);
    }

    /**
     * Destroys an entity from a player
     * @param sendTo The players the packet should be sent to
     */
    public static void sendEntityDestroyPacket(final List<Integer> ids, @NotNull List<Player> sendTo) {
        NMSHandlers.getHandler().getPacketHandler().sendEntityDestroyPacket(new IntArrayList(ids), sendTo);
    }

    /**
     * Sends a camera packet
     * @param entityId The Entity ID that camera will go towards
     * @param sendTo The players that will be sent this packet
     */
    public static void sendCameraPacket(final int entityId, @NotNull List<Player> sendTo) {
        NMSHandlers.getHandler().getPacketHandler().sendCameraPacket(entityId, sendTo);
        MessagesUtil.sendDebugMessages(sendTo + " | " + entityId + " has had a camera packet on them!");
    }

    public static void sendLeashPacket(
            final int leashedEntity,
            final int entityId,
            final @NotNull List<Player> sendTo
    ) {
        NMSHandlers.getHandler().getPacketHandler().sendLeashPacket(leashedEntity, entityId, sendTo);
    }

    /**
     * Used when a player is sent 8+ blocks.
     * @param entityId Entity this affects
     * @param location Location a player is being teleported to
     * @param onGround If the packet is on the ground
     * @param sendTo Whom to send the packet to
     */
    public static void sendTeleportPacket(
            final int entityId,
            final @NotNull Location location,
            boolean onGround,
            final @NotNull List<Player> sendTo
    ) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        NMSHandlers.getHandler().getPacketHandler().sendTeleportPacket(entityId, x, y, z, yaw, pitch, onGround, sendTo);
    }



    @NotNull
    public static List<Player> getViewers(Location location, int distance) {
        ArrayList<Player> viewers = new ArrayList<>();
        if (distance <= 0) {
            viewers.addAll(location.getWorld().getPlayers());
        } else {
            viewers.addAll(getNearbyPlayers(location, distance));
        }
        return viewers;
    }

    public static void slotUpdate(
            Player player,
            int slot
    ) {
        NMSHandlers.getHandler().getPacketHandler().sendSlotUpdate(player, slot);
    }

    public static void equipmentSlotUpdate(
            int entityId,
            org.bukkit.inventory.EquipmentSlot slot,
            ItemStack item,
            List<Player> sendTo
    ) {
        NMSHandlers.getHandler().getPacketHandler().sendEquipmentSlotUpdate(entityId, slot, item, sendTo);
    }

    public static void equipmentSlotUpdate(
            int entityId,
            HashMap<EquipmentSlot, ItemStack> equipment,
            List<Player> sendTo
    ) {
        NMSHandlers.getHandler().getPacketHandler().sendEquipmentSlotUpdate(entityId, equipment, sendTo);
    }

    private static List<Player> getNearbyPlayers(Location location, int distance) {
        List<Player> players = new ArrayList<>();
        for (Entity entity : location.getWorld().getNearbyEntities(location, distance, distance, distance)) {
            if (entity instanceof Player) {
                players.add((Player) entity);
            }
        }
        return players;
    }

}
