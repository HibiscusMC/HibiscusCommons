package me.lojosho.hibiscuscommons.nms.v1_21_R2;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSUtils extends NMSCommon implements me.lojosho.hibiscuscommons.nms.NMSUtils {

    @Override
    public int getNextEntityId() {
        return net.minecraft.world.entity.Entity.nextEntityId();
    }

    @Override
    public int getInventoryId(Player bukkitPlayer) {
        ServerPlayer player = ((CraftPlayer) bukkitPlayer).getHandle();
        return player.inventoryMenu.containerId;
    }

    @Override
    public int incrementInventoryStateId(Player bukkitPlayer) {
        ServerPlayer player = ((CraftPlayer) bukkitPlayer).getHandle();
        return player.inventoryMenu.incrementStateId();
    }
}
