package me.lojosho.hibiscuscommons.util;

import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public final class FoliaScheduler {

    private FoliaScheduler() {
    }

    public static void runEntityTaskLater(Entity entity, Runnable task, long delayTicks) {
        Plugin plugin = HibiscusCommonsPlugin.getInstance();
        if (!HibiscusCommonsPlugin.isOnFolia()) {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
            return;
        }

        try {
            Object scheduler = entity.getClass().getMethod("getScheduler").invoke(entity);
            Method runDelayed = scheduler.getClass().getMethod("runDelayed", Plugin.class, Consumer.class, Runnable.class, long.class);
            runDelayed.invoke(scheduler, plugin, (Consumer<Object>) scheduledTask -> task.run(), null, delayTicks);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Failed to schedule entity task on Folia.", exception);
        }
    }
}
