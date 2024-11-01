package me.lojosho.hibiscuscommons.util.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import it.unimi.dsi.fastutil.ints.IntList;
import me.lojosho.hibiscuscommons.nms.NMSHandlers;
import me.lojosho.hibiscuscommons.util.MessagesUtil;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
        NMSHandlers.getHandler().entitySpawn(entityId, uuid, entityType, location, sendTo);
    }

    /**
     * Destroys an entity from a player
     * @param entityId The entity to delete for a player
     * @param sendTo The players the packet should be sent to
     */
    public static void sendEntityDestroyPacket(
            final int entityId,
            final @NotNull List<Player> sendTo
    ) {
        NMSHandlers.getHandler().entityDestroy(entityId, sendTo);
    }

    /**
     * Destroys a list of entities from a player
     * @param entityIds The entities to delete for a player
     * @param sendTo The players the packet should be sent to
     */
    public static void sendEntityDestroyPacket(
            final IntList entityIds,
            final @NotNull List<Player> sendTo
    ) {
        NMSHandlers.getHandler().entityDestroy(entityIds, sendTo);
    }

    public static void sendItemDisplayMetadataPacket(
            final int entityId,
            final Vector3f translation,
            final Vector3f scale,
            final Quaternionf rotationLeft,
            final Quaternionf rotationRight,
            final Display.Billboard billboard,
            final int blockLight,
            final int skyLight,
            final float viewRange,
            final float width,
            final float height,
            final ItemDisplay.ItemDisplayTransform transform,
            final ItemStack itemStack,
            final List<Player> sendTo
    ) {
        NMSHandlers.getHandler().itemDisplayMetadata(entityId, translation, scale, rotationLeft, rotationRight, billboard, blockLight, skyLight, viewRange, width, height, transform, itemStack, sendTo);
    }

    public static void gamemodeChangePacket(
            Player player,
            int gamemode
    ) {
        NMSHandlers.getHandler().gamemodeChange(player, gamemode);
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        packet.getGameStateIDs().write(0, 3);
        // Tells what event this is. This is a change gamemode event.
        packet.getFloat().write(0, (float) gamemode);
        sendPacket(player, packet);
        MessagesUtil.sendDebugMessages("Gamemode Change sent to " + player + " to be " + gamemode);
    }

    public static void ridingMountPacket(
            int mountId,
            int passengerId,
            @NotNull List<Player> sendTo
    ) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.MOUNT);
        packet.getIntegers().write(0, mountId);
        packet.getIntegerArrays().write(0, new int[]{passengerId});
        for (Player p : sendTo) sendPacket(p, packet);
    }

    public static void sendLookPacket(
            int entityId,
            @NotNull Location location,
            @NotNull List<Player> sendTo
    ) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        packet.getIntegers().write(0, entityId);
        packet.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
        for (Player p : sendTo) sendPacket(p, packet);
    }

    public static void sendRotationPacket(
            int entityId,
            @NotNull Location location,
            boolean onGround,
            @NotNull List<Player> sendTo
    ) {
        float ROTATION_FACTOR = 256.0F / 360.0F;
        float yaw = location.getYaw() * ROTATION_FACTOR;
        float pitch = location.getPitch() * ROTATION_FACTOR;
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
        packet.getIntegers().write(0, entityId);
        packet.getBytes().write(0, (byte) yaw);
        packet.getBytes().write(1, (byte) pitch);

        //Bukkit.getLogger().info("DEBUG: Yaw: " + (location.getYaw() * ROTATION_FACTOR) + " | Original Yaw: " + location.getYaw());
        packet.getBooleans().write(0, onGround);
        for (Player p : sendTo) sendPacket(p, packet);
    }

    public static void sendRotationPacket(
            int entityId,
            int yaw,
            boolean onGround,
            @NotNull List<Player> sendTo
    ) {
        float ROTATION_FACTOR = 256.0F / 360.0F;
        float yaw2 = yaw * ROTATION_FACTOR;
        NMSHandlers.getHandler().rotation(entityId, yaw2, onGround, sendTo);
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
        NMSHandlers.getHandler().mount(mountId, passengerIds, sendTo);
    }

    /**
     * Sends a camera packet
     * @param entityId The Entity ID that camera will go towards
     * @param sendTo The players that will be sent this packet
     */
    public static void sendCameraPacket(final int entityId, @NotNull List<Player> sendTo) {
        NMSHandlers.getHandler().camera(entityId, sendTo);
        MessagesUtil.sendDebugMessages(sendTo + " | " + entityId + " has had a camera packet on them!");
    }

    public static void sendLeashPacket(
            final int leashedEntity,
            final int entityId,
            final @NotNull List<Player> sendTo
    ) {
        NMSHandlers.getHandler().leash(leashedEntity, entityId, sendTo);
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
        NMSHandlers.getHandler().teleport(entityId, location, onGround, sendTo);
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
        NMSHandlers.getHandler().slotUpdate(player, slot);
    }

    public static void equipmentSlotUpdate(
            int entityId,
            org.bukkit.inventory.EquipmentSlot slot,
            ItemStack item,
            List<Player> sendTo
    ) {
        NMSHandlers.getHandler().equipmentSlotUpdate(entityId, slot, item, sendTo);
    }

    public static void equipmentSlotUpdate(
            int entityId,
            HashMap<EquipmentSlot, ItemStack> equipment,
            List<Player> sendTo
    ) {
        NMSHandlers.getHandler().equipmentSlotUpdate(entityId, equipment, sendTo);
    }

    private static List<Player> getNearbyPlayers(Location location, int distance) {
        List<Player> players = new ArrayList<>();
        for (Entity entity : location.getWorld().getNearbyEntities(location, distance, distance, distance)) {
            if (entity instanceof Player player) players.add(player);
        }
        return players;
    }

}
