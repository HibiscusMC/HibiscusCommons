package me.lojosho.hibiscuscommons.util.packets;

import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;

public class PEConverter {

    public static EquipmentSlot convertEquipmentSlot(org.bukkit.inventory.EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> EquipmentSlot.HELMET;
            case CHEST -> EquipmentSlot.CHEST_PLATE;
            case LEGS -> EquipmentSlot.LEGGINGS;
            case FEET -> EquipmentSlot.BOOTS;
            case HAND -> EquipmentSlot.MAIN_HAND;
            case OFF_HAND -> EquipmentSlot.OFF_HAND;
            default -> EquipmentSlot.valueOf(slot.name());
        };
    }
}
