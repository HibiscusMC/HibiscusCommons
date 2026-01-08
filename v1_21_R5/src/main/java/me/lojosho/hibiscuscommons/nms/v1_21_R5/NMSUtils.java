package me.lojosho.hibiscuscommons.nms.v1_21_R5;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NMSUtils implements me.lojosho.hibiscuscommons.nms.NMSUtils {

    @Override
    public int getNextEntityId() {
        return net.minecraft.world.entity.Entity.nextEntityId();
    }

    @Override
    public org.bukkit.entity.Entity getEntity(int entityId) {
        net.minecraft.world.entity.Entity entity = getNMSEntity(entityId);
        if (entity == null) return null;
        return entity.getBukkitEntity();
    }

    @Override
    public @Nullable Color getColor(ItemStack itemStack) {
        if (itemStack == null) return null;
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItem == null) return null;

        DyedItemColor color = nmsItem.get(DataComponents.DYED_COLOR);
        if (color == null) return null;
        return Color.fromRGB(color.rgb());
    }

    @Override
    public ItemStack setColor(@NotNull ItemStack itemStack, @NotNull Color color) {
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        // Potions needs a bit extra to get the color working properly
        if (itemStack.getType() == Material.POTION) {
            nmsStack.set(DataComponents.POTION_CONTENTS, new PotionContents(
                    Optional.empty(),
                    Optional.of(color.asRGB()),
                    List.of(),
                    Optional.empty())
            );
        }
        nmsStack.set(DataComponents.DYED_COLOR, new DyedItemColor(color.asRGB()));
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    private net.minecraft.world.entity.Entity getNMSEntity(int entityId) {
        for (ServerLevel world : ((CraftServer) Bukkit.getServer()).getHandle().getServer().getAllLevels()) {
            net.minecraft.world.entity.Entity entity = world.getEntity(entityId);
            if (entity == null) continue;
            return entity;
        }
        return null;
    }

    @Override
    public void handleChannelOpen(@NotNull Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        ChannelPipeline pipeline = channel.pipeline();

        NMSPacketChannel channelHandler = new NMSPacketChannel(player);
        for (String key : pipeline.toMap().keySet()) {
            if (!(pipeline.get(key) instanceof Connection)) continue;
            pipeline.addBefore(key, "hibiscus_channel_handler", channelHandler);
        }
    }

    public void sendToastAdvancement(Player player, ItemStack icon, Component title, Component description) {
        final var key = ResourceLocation.fromNamespaceAndPath("hibiscuscommons", UUID.randomUUID().toString());

        JsonObject json = new JsonObject();

        // Creating the "criteria" object
        JsonObject impossibleCriteria = new JsonObject();
        JsonObject impossible = new JsonObject();
        impossible.addProperty("trigger", "minecraft:impossible");
        impossibleCriteria.add("impossible", impossible);
        json.add("criteria", impossibleCriteria);

        // Creating the "display" object
        JsonObject display = new JsonObject();
        JsonObject iconObj = new JsonObject();
        iconObj.addProperty("id", icon.getType().getKey().toString());

        if (icon.hasItemMeta()) {
            ItemMeta meta = icon.getItemMeta();
            JsonObject components = new JsonObject();

            if (!meta.getEnchants().isEmpty()) {
                components.addProperty("minecraft:enchantment_glint_override", true);
            }

            if (meta.hasCustomModelData()) {
                CustomModelDataComponent customModelDataComponent = meta.getCustomModelDataComponent();
                JsonObject customModelDataComponentJson = new JsonObject();

                List<Float> floats = customModelDataComponent.getFloats();
                if (!floats.isEmpty()) {
                    JsonArray floatsArray = new JsonArray();
                    floats.forEach(floatsArray::add);
                    customModelDataComponentJson.add("floats", floatsArray);
                }

                List<Boolean> flags = customModelDataComponent.getFlags();
                if (!flags.isEmpty()) {
                    JsonArray flagsArray = new JsonArray();
                    flags.forEach(flagsArray::add);
                    customModelDataComponentJson.add("flags", flagsArray);
                }

                List<String> strings = customModelDataComponent.getStrings();
                if (!strings.isEmpty()) {
                    JsonArray stringsArray = new JsonArray();
                    strings.forEach(stringsArray::add);
                    customModelDataComponentJson.add("strings", stringsArray);
                }

                List<Color> colors = customModelDataComponent.getColors();
                if (!colors.isEmpty()) {
                    JsonArray colorsArray = new JsonArray();
                    colors.forEach(color -> colorsArray.add(color.asRGB()));
                    customModelDataComponentJson.add("colors", colorsArray);
                }

                components.add("minecraft:custom_model_data", customModelDataComponentJson);
            }

            NamespacedKey itemModel = meta.getItemModel();
            if (itemModel != null) {
                components.addProperty("minecraft:item_model", itemModel.toString());
            }

            iconObj.add("components", components);
        }

        display.add("icon", iconObj);
        display.add("title", GsonComponentSerializer.gson().serializeToTree(title));
        display.add("description", GsonComponentSerializer.gson().serializeToTree(description));
        display.addProperty("description", "Toast Description");
        display.addProperty("frame", "task");
        display.addProperty("announce_to_chat", false);
        display.addProperty("show_toast", true);
        display.addProperty("hidden", true);

        json.add("display", display);

        final var advancement = Advancement.CODEC.parse(MinecraftServer.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE), json);
        final var advancementHolder = new AdvancementHolder(key, advancement.result().orElseThrow());

        final var nmsPlayer = ((CraftPlayer) player).getHandle();
        final var progress = nmsPlayer.getAdvancements().getOrStartProgress(advancementHolder);
        MinecraftServer.getServer().getAdvancements().tree().addAll(Set.of(advancementHolder));
        progress.getRemainingCriteria().forEach(criteria -> nmsPlayer.getAdvancements().award(advancementHolder, criteria));


        Bukkit.getScheduler().runTaskLater(HibiscusCommonsPlugin.getInstance(), () -> {
            progress.getRemainingCriteria().forEach(criteria -> nmsPlayer.getAdvancements().revoke(advancementHolder, criteria));
            MinecraftServer.getServer().getAdvancements().tree().remove(Set.of(key));

            // Remove the advancement from the player's client to prevent it from being displayed again
            // Was not working without this?
            ClientboundUpdateAdvancementsPacket removePacket = new ClientboundUpdateAdvancementsPacket(
                    false,
                    Collections.emptyList(),
                    Set.of(key),
                    Map.of(),
                    false
            );

            nmsPlayer.connection.send(removePacket);
        }, 2L);
    }
}
