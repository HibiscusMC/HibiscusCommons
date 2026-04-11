package me.lojosho.hibiscuscommons.util;

import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public final class FoliaScheduler {

    private FoliaScheduler() {
    }

    public static void runEntityTaskLater(Entity entity, Runnable task, long delayTicks) {
        Plugin plugin = HibiscusCommonsPlugin.getInstance();
        if (!HibiscusCommonsPlugin.isOnFolia()) {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
            return;
        }

        if (!entity.getScheduler().execute(plugin, task, null, delayTicks)) {
            throw new IllegalStateException("Failed to schedule entity task on Folia.");
        }
    }
}
