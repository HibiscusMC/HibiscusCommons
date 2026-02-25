package me.lojosho.hibiscuscommons.hooks.items;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.lojosho.hibiscuscommons.hooks.Hook;
import me.lojosho.hibiscuscommons.hooks.HookFlag;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A hook that integrates the plugin {@link MythicBukkit MythicBukkit} to provide custom items
 */
@SuppressWarnings("SpellCheckingInspection")
public class HookMythic extends Hook {
    public HookMythic() {
        super("mythicmobs", HookFlag.ITEM_SUPPORT, HookFlag.ENTITY_SUPPORT);
        setActive(true);
    }

    /**
     * Gets a cosmetic {@link ItemStack} that is associated with the provided id from the plugin {@link MythicBukkit MythicBukkit}
     */
    @Override
    public ItemStack getItem(@NotNull String itemId) {
        return MythicBukkit.inst().getItemManager().getItemStack(itemId);
    }

    @Override
    public String getEntityString(Entity entity) {
        ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).orElse(null);
        if(mythicMob != null){
            return mythicMob.getMobType();
        }
        return null;
    }
}
