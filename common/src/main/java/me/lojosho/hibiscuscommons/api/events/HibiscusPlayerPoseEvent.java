package me.lojosho.hibiscuscommons.api.events;

import me.lojosho.hibiscuscommons.hooks.Hook;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HibiscusPlayerPoseEvent extends HibiscusHookPlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private boolean gettingUp = false;

    public HibiscusPlayerPoseEvent(Hook hook, Player player, boolean gettingUp) {
        super(hook, player);
        this.gettingUp = gettingUp;
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

    /**
     * Returns whether the player is getting up from a laying down pose.
     * @return If false, the player is going into a laying down position. If true, the player is getting up from a laying down position.
     */
    public boolean isGettingUp() {
        return gettingUp;
    }
}
