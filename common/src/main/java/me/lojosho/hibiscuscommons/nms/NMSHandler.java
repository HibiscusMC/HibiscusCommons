package me.lojosho.hibiscuscommons.nms;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

    void entitySpawn(int entityId, UUID uuid, EntityType entityType, Location location, List<Player> sendTo);

    void entityDestroy(IntList entityIds, List<Player> sendTo);
    void entityDestroy(int entityId, List<Player> sendTo);

    void hideNPCName(
            Player player,
            String NPCName
    );

    default boolean getSupported () {
        return false;
    }

}
