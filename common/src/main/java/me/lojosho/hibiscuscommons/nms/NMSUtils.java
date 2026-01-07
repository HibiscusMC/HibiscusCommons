package me.lojosho.hibiscuscommons.nms;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface NMSUtils {

    int getNextEntityId();

    Entity getEntity(int entityId);

    @Nullable
    Color getColor(ItemStack itemStack);

    ItemStack setColor(@NotNull ItemStack itemStack, Color color);

    default void handleChannelOpen(@NotNull Player player) {

    }

    void sendToastAdvancement(Player player, ItemStack icon, Component title, Component description);

}
