package me.lojosho.hibiscuscommons.nms.v1_21_R6.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerSlotUpdateWrapper implements PacketWrapper {

    private final Player player;
    private final int slot;

    public PlayerSlotUpdateWrapper(Player player, int slot) {
        this.player = player;
        this.slot = slot;
    }

    @Override
    public PacketType getType() {
        return PacketType.PLAYER_SLOT_UPDATE;
    }

    @Override
    public Object toNativePacket() {
        int index = 0;

        ServerPlayer player1 = ((CraftPlayer) player).getHandle();

        if (index < Inventory.getSelectionSize()) {
            index += 36;
        } else if (index > 39) {
            index += 5; // Off hand
        } else if (index > 35) {
            index = 8 - (index - 36);
        }
        ItemStack item = player.getInventory().getItem(slot);

        return new ClientboundContainerSetSlotPacket(player1.inventoryMenu.containerId, player1.inventoryMenu.incrementStateId(), index, CraftItemStack.asNMSCopy(item));
    }
}