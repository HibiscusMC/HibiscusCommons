package me.lojosho.hibiscuscommons.api.events;

import me.lojosho.hibiscuscommons.hooks.Hook;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HibiscusPlayerUnVanishEvent extends HibiscusHookPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancel = false;

    public HibiscusPlayerUnVanishEvent(Hook hook, Player player) {
        super(hook, player);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
