package me.lojosho.hibiscuscommons.hooks.items;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import com.nexomc.nexo.items.ItemBuilder;
import me.lojosho.hibiscuscommons.api.events.HibiscusHookReload;
import me.lojosho.hibiscuscommons.hooks.Hook;
import me.lojosho.hibiscuscommons.hooks.HookFlag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 * A hook that integrates the plugin {@link com.nexomc.nexo.NexoPlugin NexoPlugin} to provide custom items
 */
@SuppressWarnings("SpellCheckingInspection")
public class HookNexo extends Hook {

    public HookNexo() {
        super("nexo", HookFlag.ITEM_SUPPORT, HookFlag.LATE_LOAD);
    }

    /**
     * Gets a cosmetic {@link ItemStack} that is associated with the provided id from the plugin {@link com.nexomc.nexo.NexoPlugin NexoPlugin}
     */
    @Override
    public ItemStack getItem(@NotNull String itemId) {
        return NexoItems.optionalItemFromId(itemId).map(ItemBuilder::build).orElse(isActive() ? new ItemStack(Material.AIR) : null);
    }

    @Override
    public String getItemString(@NonNull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return null;
        return NexoItems.idFromItem(itemStack);
    }

    @EventHandler
    public void onLoadItems(NexoItemsLoadedEvent event) {
        HibiscusHookReload.ReloadType type = HibiscusHookReload.ReloadType.RELOAD;
        if (!isActive()) {
            type = HibiscusHookReload.ReloadType.INITIAL;
            setActive(true);
        }

        Bukkit.getPluginManager().callEvent(new HibiscusHookReload(this, type));
    }
}