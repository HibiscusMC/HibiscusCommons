package me.lojosho.hibiscuscommons.util;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import me.lojosho.hibiscuscommons.nms.MinecraftVersion;
import me.lojosho.hibiscuscommons.nms.NMSHandlers;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;
    private final boolean onPaper = HibiscusCommonsPlugin.isOnPaper();
    @Getter
    private final Material material;

    public ItemBuilder(@NotNull Material material) {
        this.material = material;
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
        this.material = itemStack.getType();
    }

    public ItemBuilder(@NotNull ItemStack itemStack, @NotNull ItemMeta itemMeta) {
        this.itemStack = itemStack;
        this.itemMeta = itemMeta;
        this.material = itemStack.getType();
    }

    public ItemBuilder setDisplayName(@NotNull String displayName) {
        if (onPaper) itemMeta.displayName(AdventureUtils.MINI_MESSAGE.deserialize(displayName));
        else itemMeta.setDisplayName(StringUtils.parseStringToString(displayName));
        return this;
    }

    public ItemBuilder setDisplayName(@NotNull Component displayName) {
        if (onPaper) itemMeta.displayName(displayName);
        return this;
    }

    public ItemBuilder setDisplayNameRaw(@NotNull String displayNameRaw) {
        if (onPaper) itemMeta.displayName(Component.text(displayNameRaw));
        else itemMeta.setDisplayName(displayNameRaw);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setCustomModelId(int number) {
        if (onPaper && NMSHandlers.getVersion().isHigherOrEqual(MinecraftVersion.v1_21_5)) {
            CustomModelDataComponent modelDataComponent = itemMeta.getCustomModelDataComponent();
            modelDataComponent.setFloats(List.of((float) number));
            itemMeta.setCustomModelDataComponent(modelDataComponent);
        }
        else itemMeta.setCustomModelData(number);
        return this;
    }

    public ItemBuilder setGlowing(boolean glowing) {
        if (glowing) {
            itemMeta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder setLore(@NotNull List<String> lore) {
        if (HibiscusCommonsPlugin.isOnPaper()) {
            itemMeta.lore(lore.
                    stream().map(AdventureUtils.MINI_MESSAGE::deserialize).collect(Collectors.toList()));
        } else {
            itemMeta.setLore(lore.
                    stream().map(StringUtils::parseStringToString).collect(Collectors.toList()));
        }
        return this;
    }

    public ItemBuilder setQuantity(int number) {
        itemStack.setAmount(number);
        return this;
    }

    public ItemBuilder setModelItemId(@NotNull String itemModelId) {
        // If lower than 1.21.4, ignore
        if (NMSHandlers.getVersion().isLower(MinecraftVersion.v1_21_4)) return this;
        String stringKey = HibiscusCommonsPlugin.getInstance().getName();
        if (itemModelId.contains(":")) {
            String[] split = itemModelId.split(":");
            itemModelId = split[1];
            stringKey = split[0];
        }
        if (!itemModelId.isEmpty()) {
            NamespacedKey key = new NamespacedKey(stringKey, itemModelId);
            itemMeta.setItemModel(key);
        } else {
            MessagesUtil.sendDebugMessages("Could not find item model id for " + stringKey + " in " + itemModelId);
        }
        return this;
    }

    public ItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder setItemFlag(ItemFlag itemFlag) {
        //if (HibiscusCommonsPlugin.isOnPaper() && NMSHandlers.getVersion().isHigherOrEqual(MinecraftVersion.v1_20_6)) {
        //    itemMeta.setAttributeModifiers(item.getType().getDefaultAttributeModifiers());
        //}

        itemMeta.addItemFlags(itemFlag);
        return this;
    }

    public ItemBuilder addEnchantment(@NotNull String enchantName, int level) {
        enchantName = enchantName.toLowerCase();
        NamespacedKey key = NamespacedKey.minecraft(enchantName);
        Enchantment enchant = null;

        if (HibiscusCommonsPlugin.isOnPaper() && NMSHandlers.getVersion().isHigherOrEqual(MinecraftVersion.v1_21_4)) {
            enchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key);
        } else {
            enchant = Registry.ENCHANTMENT.get(key);
        }
        if (enchant == null) return this;
        addEnchantment(enchant, level);
        return this;
    }

    public ItemBuilder setToolTip(@Nullable NamespacedKey key) {
        itemMeta.setTooltipStyle(key);
        return this;
    }

    public ItemBuilder removeAllEnchantments() {
        itemMeta.removeEnchantments();
        return this;
    }

    public @NotNull ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
