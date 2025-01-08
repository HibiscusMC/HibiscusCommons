package me.lojosho.hibiscuscommons.nms;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface NMSUtils {

    int getNextEntityId();

    /**
     * @deprecated Use {@link NMSHandler#getEntity(int)}
     */
    @Deprecated
    default Entity getEntity(int entityId) {
        return NMSHandlers.getHandler().getEntity(entityId);
    }

    int getInventoryId(Player bukkitPlayer);

    int incrementInventoryStateId(Player bukkitPlayer);
}
