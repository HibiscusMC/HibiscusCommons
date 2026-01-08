package me.lojosho.hibiscuscommons.nms.v1_21_R1.packets.wrapper;

import com.mojang.datafixers.util.Pair;
import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityEquipmentSlotUpdateWrapper implements PacketWrapper {

    private final int entityId;
    private final Map<EquipmentSlot, ItemStack> equipment;

    public EntityEquipmentSlotUpdateWrapper(int entityId, Map<EquipmentSlot, ItemStack> equipment) {
        this.entityId = entityId;
        this.equipment = equipment;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_EQUIPMENT_SLOT_UPDATE;
    }

    @Override
    public Object toNativePacket() {
        // Converting EquipmentSlot and ItemStack to NMS ones.
        final List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> pairs = new ArrayList<>();

        for (EquipmentSlot slot : equipment.keySet()) {
            net.minecraft.world.entity.EquipmentSlot nmsSlot = CraftEquipmentSlot.getNMS(slot);
            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(equipment.get(slot));

            Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack> pair = new Pair<>(nmsSlot, nmsItem);
            pairs.add(pair);
        }

        return new ClientboundSetEquipmentPacket(entityId, pairs);
    }
}
