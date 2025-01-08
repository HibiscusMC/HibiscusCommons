package me.lojosho.hibiscuscommons.nms;

import it.unimi.dsi.fastutil.ints.IntList;
import me.lojosho.hibiscuscommons.util.packets.PacketManager;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @deprecated Move to {@link PacketManager}
 */
@Deprecated
public class NMSPackets {

    /**
     * @deprecated Move to {@link PacketManager#slotUpdate(Player, int)}
     */
    @Deprecated
    public void sendSlotUpdate(
            Player player,
            int slot
    ) {
        PacketManager.slotUpdate(player, slot);
    }

    /**
     * @deprecated Move to {@link PacketManager#equipmentSlotUpdate(int, EquipmentSlot, ItemStack, List)}
     */
    @Deprecated
    public void sendEquipmentSlotUpdate(
            int entityId,
            org.bukkit.inventory.EquipmentSlot slot,
            ItemStack item,
            List<Player> sendTo
    ) {
        PacketManager.equipmentSlotUpdate(entityId, slot, item, sendTo);
    }

    /**
     * @deprecated Move to {@link PacketManager#equipmentSlotUpdate(int, HashMap, List)}
     */
    @Deprecated
    public void sendEquipmentSlotUpdate(
            int entityId,
            HashMap<EquipmentSlot, ItemStack> equipment,
            List<Player> sendTo
    ) {
        PacketManager.equipmentSlotUpdate(entityId, equipment, sendTo);
    }

    /**
     * @deprecated Move to {@link PacketManager#sendScoreboardHideNamePacket(Player, String)}
     */
    @Deprecated
    public void sendScoreboardHideNamePacket(
            Player player,
            String name
    ) {
        PacketManager.sendScoreboardHideNamePacket(player, name);
    }

    /**
     * @deprecated Move to {@link PacketManager#ridingMountPacket(int, int, List)}
     */
    @Deprecated
    public void sendMountPacket(int mountId, int[] passengerIds, List<Player> sendTo) {
        for (int passengerId : passengerIds) {
            PacketManager.ridingMountPacket(mountId, passengerId, sendTo);
        }
    }

    /**
     * @deprecated Move to {@link PacketManager#sendLeashPacket(int, int, List)}
     */
    @Deprecated
    public void sendLeashPacket(int leashEntity, int entityId, List<Player> sendTo) {
        PacketManager.sendLeashPacket(leashEntity, entityId, sendTo);
    }

    /**
     * @deprecated Move to {@link PacketManager#sendTeleportPacket(int, Location, boolean, List)}
     */
    @Deprecated
    public void sendTeleportPacket(
            int entityId,
            double x,
            double y,
            double z,
            float yaw,
            float pitch,
            boolean onGround,
            List<Player> sendTo
    ) {
        PacketManager.sendTeleportPacket(
            entityId,
            new Location(null, x, y, z, yaw, pitch),
            onGround,
            sendTo
        );
    }

    /**
     * @deprecated Move to {@link PacketManager#sendRotationPacket(int, int, boolean, List)}
     */
    @Deprecated
    public void sendRotationPacket(int entityId, float yaw, boolean onGround, List<Player> sendTo) {
        PacketManager.sendRotationPacket(entityId, (int) yaw, onGround, sendTo);
    }

    /**
     * @deprecated Move to {@link PacketManager#sendCameraPacket(int, List)}
     */
    @Deprecated
    public void sendCameraPacket(int entityId, List<Player> sendTo) {
        PacketManager.sendCameraPacket(entityId, sendTo);
    }

    /**
     * @deprecated Move to {@link PacketManager#sendEntitySpawnPacket(Location, int, EntityType, UUID, List)}
     */
    @Deprecated
    public void sendSpawnEntityPacket(int entityId, UUID uuid, EntityType entityType, Location location, List<Player> sendTo) {
        PacketManager.sendEntitySpawnPacket(location, entityId, entityType, uuid, sendTo);
    }

    /**
     * @deprecated Move to {@link PacketManager#sendEntityDestroyPacket(int, List)}
     */
    @Deprecated
    public void sendEntityDestroyPacket(IntList entityIds, List<Player> sendTo) {
        PacketManager.sendEntityDestroyPacket(entityIds, sendTo);
    }

    /**
     * @deprecated Move to {@link PacketManager#sendItemDisplayMetadata(int, Vector3f, Vector3f, Quaternionf, Quaternionf, Display.Billboard, int, int, float, float, float, ItemDisplay.ItemDisplayTransform, ItemStack, List)}
     */
    @Deprecated
    public void sendItemDisplayMetadata(int entityId,
                                 Vector3f translation,
                                 Vector3f scale,
                                 Quaternionf rotationLeft,
                                 Quaternionf rotationRight,
                                 Display.Billboard billboard,
                                 int blockLight, int skyLight, float viewRange, float width, float height,
                                 ItemDisplay.ItemDisplayTransform transform, ItemStack itemStack,
                                 List<Player> sendTo) {
        PacketManager.sendItemDisplayMetadata(entityId, translation, scale, rotationLeft, rotationRight, billboard,
            blockLight, skyLight, viewRange, width, height, transform, itemStack, sendTo);
    }
}
