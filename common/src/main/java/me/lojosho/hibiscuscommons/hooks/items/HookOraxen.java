package me.lojosho.hibiscuscommons.hooks.items;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import me.lojosho.hibiscuscommons.hooks.Hook;
import me.lojosho.hibiscuscommons.hooks.HookFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 * A hook that integrates the plugin {@link io.th0rgal.oraxen.OraxenPlugin OraxenPlugin} to provide custom items
 */
@SuppressWarnings("SpellCheckingInspection")
public class HookOraxen extends Hook {
    public HookOraxen() {
        super("oraxen", HookFlag.ITEM_SUPPORT);
        setActive(true);
    }

    /**
     * Gets a cosmetic {@link ItemStack} that is associated with the provided id from the plugin {@link io.th0rgal.oraxen.OraxenPlugin OraxenPlugin}
     */
    @Override
    public ItemStack getItem(@NotNull String itemId) {
        ItemBuilder builder = OraxenItems.getItemById(itemId);
        if (builder == null) return null;
        return builder.build().clone(); // Prevent us from modifying the original item
    }

    @Override
    public String getItemString(@NonNull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return null;
        if (!OraxenItems.exists(itemStack)) return null;
        return OraxenItems.getIdByItem(itemStack);
    }
}
