package me.lojosho.hibiscuscommons.nms;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public interface NMSHandler {

    int getNextEntityId();

    Entity getEntity(int entityId);

    void slotUpdate(
            Player player,
            int slot
    );

    void equipmentSlotUpdate(
            int entityId,
            org.bukkit.inventory.EquipmentSlot slot,
            ItemStack item,
            List<Player> sendTo
    );

    void equipmentSlotUpdate(
            int entityId,
            HashMap<EquipmentSlot, ItemStack> equipment,
            List<Player> sendTo
    );

    void entitySpawn(int entityId, EntityType entityType, Location location, List<Player> sendTo);

    void hideNPCName(
            Player player,
            String NPCName
    );

    default boolean getSupported () {
        return false;
    }

}
