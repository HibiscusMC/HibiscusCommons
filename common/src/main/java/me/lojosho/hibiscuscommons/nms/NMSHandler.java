package me.lojosho.hibiscuscommons.nms;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public interface NMSHandler {

    int getNextEntityId();

    default Entity getEntity(int entityId) {
        for (World world : Bukkit.getWorlds()) {
            Entity entity = SpigotConversionUtil.getEntityById(world, entityId);
            if (entity != null) {
                return entity;
            }
        }

        return null;
    }

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

    void hideNPCName(
            Player player,
            String NPCName
    );

    default boolean getSupported () {
        return false;
    }

}
