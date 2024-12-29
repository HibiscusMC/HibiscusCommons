package me.lojosho.hibiscuscommons.hooks.misc;

import dev.geco.gsit.api.event.PlayerGetUpPoseEvent;
import dev.geco.gsit.api.event.PlayerPoseEvent;
import me.lojosho.hibiscuscommons.api.events.HibiscusPlayerPoseEvent;
import me.lojosho.hibiscuscommons.hooks.Hook;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class HookGSit extends Hook {

    public HookGSit() {
        super("GSit");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerLay(PlayerPoseEvent event) {
        HibiscusPlayerPoseEvent newEvent = new HibiscusPlayerPoseEvent(this, event.getPlayer(), false);
        Bukkit.getPluginManager().callEvent(newEvent);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerGetUp(PlayerGetUpPoseEvent event) {
        HibiscusPlayerPoseEvent newEvent = new HibiscusPlayerPoseEvent(this, event.getPlayer(), true);
        Bukkit.getPluginManager().callEvent(newEvent);
    }
}
