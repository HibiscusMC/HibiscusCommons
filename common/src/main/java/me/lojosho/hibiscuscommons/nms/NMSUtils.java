package me.lojosho.hibiscuscommons.nms;

import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface NMSUtils {

    int getNextEntityId();

    Entity getEntity(int entityId);

    @Nullable
    Color getColor(ItemStack itemStack);

    ItemStack setColor(@NotNull ItemStack itemStack, Color color);

}
