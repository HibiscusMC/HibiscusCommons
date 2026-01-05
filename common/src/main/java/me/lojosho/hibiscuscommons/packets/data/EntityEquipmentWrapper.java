package me.lojosho.hibiscuscommons.packets.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter @Setter
public class EntityEquipmentWrapper {

    private int entityId;
    private Map<EquipmentSlot, ItemStack> armor;

    public EntityEquipmentWrapper(int entityId, @NotNull Map<EquipmentSlot, ItemStack> armor) {
        this.entityId = entityId;
        this.armor = armor;
    }
}
