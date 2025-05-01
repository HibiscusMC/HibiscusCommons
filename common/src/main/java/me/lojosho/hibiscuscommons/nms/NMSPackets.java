package me.lojosho.hibiscuscommons.nms;

import it.unimi.dsi.fastutil.ints.IntList;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
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

public interface NMSPackets {

    static int POSITION_INTERPOLATION_DURATION = 2;

    void sendGamemodeChange(Player player, GameMode gameMode);

    void sendRotateHeadPacket(int entityId, Location location, List<Player> sendTo);

    void sendRotationPacket(int entityId, Location location, boolean onGround, List<Player> sendTo);

    void sendSlotUpdate(Player player, int slot);

    void sendEquipmentSlotUpdate(
            int entityId,
            org.bukkit.inventory.EquipmentSlot slot,
            ItemStack item,
            List<Player> sendTo
    );

    void sendEquipmentSlotUpdate(
            int entityId,
            HashMap<EquipmentSlot, ItemStack> equipment,
            List<Player> sendTo
    );

    void sendScoreboardHideNamePacket(
            Player player,
            String name
    );

    void sendMountPacket(int mountId, int[] passengerIds, List<Player> sendTo);

    void sendLeashPacket(int leashEntity, int entityId, List<Player> sendTo);

    void sendTeleportPacket(
            int entityId,
            double x,
            double y,
            double z,
            float yaw,
            float pitch,
            boolean onGround,
            List<Player> sendTo
    );

    void sendCameraPacket(int entityId, List<Player> sendTo);

    void sendSpawnEntityPacket(int entityId, UUID uuid, EntityType entityType, Location location, List<Player> sendTo);

    void sendEntityDestroyPacket(IntList entityIds, List<Player> sendTo);

    void sendItemDisplayMetadata(int entityId,
                                 Vector3f translation,
                                 Vector3f scale,
                                 Quaternionf rotationLeft,
                                 Quaternionf rotationRight,
                                 Display.Billboard billboard,
                                 int blockLight, int skyLight, float viewRange, float width, float height,
                                 ItemDisplay.ItemDisplayTransform transform, ItemStack itemStack,
                                 List<Player> sendTo);

    void sendToastPacket(Player player, ItemStack icon, Component title, Component description);
}
