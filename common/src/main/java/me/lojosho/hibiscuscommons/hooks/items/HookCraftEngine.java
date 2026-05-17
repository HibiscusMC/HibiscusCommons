package me.lojosho.hibiscuscommons.hooks.items;

import me.lojosho.hibiscuscommons.api.events.HibiscusHookReload;
import me.lojosho.hibiscuscommons.hooks.Hook;
import me.lojosho.hibiscuscommons.hooks.HookFlag;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A hook that integrates the plugin {@link net.momirealms.craftengine.bukkit.plugin.BukkitCraftEngine CraftEngine} to provide custom items
 */
public class HookCraftEngine extends Hook {

    public HookCraftEngine() {
        super("craftengine", HookFlag.ITEM_SUPPORT, HookFlag.LATE_LOAD);
    }

    @Override
    public ItemStack getItem(@NotNull String itemId) {
        if (!isActive()) return new ItemStack(Material.AIR);
        BukkitItemDefinition item = CraftEngineItems.byId(Key.of(itemId));
        if (item == null) return null;
        return item.buildBukkitItem();
    }

    @Override
    public String getItemString(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return null;
        Key key = CraftEngineItems.getCustomItemId(itemStack);
        if (key == null) return null;
        return key.toString();
    }

    @EventHandler
    public void onPluginEnable(CraftEngineReloadEvent event) {
        if (event.isFirstReload() && !isActive()) {
            setActive(true);
            Bukkit.getPluginManager().callEvent(new HibiscusHookReload(this, HibiscusHookReload.ReloadType.INITIAL));
        }
    }
}
