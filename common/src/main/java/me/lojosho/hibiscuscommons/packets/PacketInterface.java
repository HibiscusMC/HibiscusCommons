package me.lojosho.hibiscuscommons.packets;

import me.lojosho.hibiscuscommons.packets.data.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PacketInterface {

    default PacketAction writeContainerContent(@NotNull Player player, @NotNull ContainerContentWrapper wrapper) {
        return PacketAction.NOTHING;
    }

    default PacketAction writeSlotContent(@NotNull Player player, @NotNull SlotContentWrapper wrapper) {
        return PacketAction.NOTHING;
    }

    default PacketAction writeEquipmentContent(@NotNull Player player, @NotNull EntityEquipmentWrapper wrapper) {
        return PacketAction.NOTHING;
    }

    default PacketAction writePassengerContent(@NotNull Player player, @NotNull PassengerWrapper wrapper) {
        return PacketAction.NOTHING;
    }

    default PacketAction readInventoryClick(@NotNull Player player, @NotNull InventoryClickWrapper wrapper) {
        return PacketAction.NOTHING;
        // Override
    }

    default PacketAction readPlayerAction(@NotNull Player player, @NotNull PlayerActionWrapper wrapper) {
        return PacketAction.NOTHING;
        // Override
    }

    default PacketAction readPlayerArm(@NotNull Player player, @NotNull PlayerSwingWrapper wrapper) {
        return PacketAction.NOTHING;
        // Override
    }

    default PacketAction readPlayerScale(@NotNull Player player, @NotNull PlayerScaleWrapper wrapper) {
        return PacketAction.NOTHING;
        // Override
    }

    default PacketAction readEntityHandle(@NotNull Player player, @NotNull PlayerInteractWrapper wrapper) {
        return PacketAction.NOTHING;
        // Override
    }
}
