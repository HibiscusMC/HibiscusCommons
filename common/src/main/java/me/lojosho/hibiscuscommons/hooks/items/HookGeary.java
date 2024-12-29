package me.lojosho.hibiscuscommons.hooks.items;

import com.mineinabyss.geary.papermc.GearyPaper;
import com.mineinabyss.geary.papermc.GearyPaperKt;
import com.mineinabyss.geary.papermc.GearyPaperModule;
import com.mineinabyss.geary.papermc.GearyPaperModuleKt;
import com.mineinabyss.geary.papermc.tracking.items.ItemTrackingKt;
import com.mineinabyss.geary.papermc.tracking.items.ItemTrackingModule;
import com.mineinabyss.geary.prefabs.PrefabKey;
import me.lojosho.hibiscuscommons.hooks.Hook;
import me.lojosho.hibiscuscommons.hooks.HookFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A hook that integrates the plugin {@link com.mineinabyss.geary.papermc.GearyPlugin Geary} to provide custom items
 */
public class HookGeary extends Hook {

    private ItemTrackingModule itemTracking = null;

    public HookGeary() {
        super("geary", HookFlag.ITEM_SUPPORT);
    }

    /**
     * Gets a cosmetic {@link ItemStack} that is associated with the provided id from the plugin {@link com.mineinabyss.geary.papermc.GearyPlugin Geary}
     */
    @Override
    public ItemStack getItem(@NotNull String itemId) {
        if (itemTracking == null) itemTracking = GearyPaperModuleKt.getGearyPaper().getWorldManager().getGlobal().getAddon(ItemTrackingKt.getItemTracking());
        PrefabKey prefabKey = PrefabKey.Companion.ofOrNull(itemId);
        if (prefabKey == null) return null;
        return itemTracking.createItem(prefabKey, null);
    }
}
