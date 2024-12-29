package me.lojosho.hibiscuscommons.nms;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public interface NMSPackets {

    void sendSlotUpdate(
            Player player,
            int slot
    );

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

    void sendRotationPacket(int entityId, float yaw, boolean onGround, List<Player> sendTo);
}
