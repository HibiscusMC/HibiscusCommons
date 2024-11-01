package me.lojosho.hibiscuscommons.api.events;

import me.lojosho.hibiscuscommons.hooks.Hook;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HibiscusPlayerEmoteEndEvent extends HibiscusHookPlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final String emoteName;

    public HibiscusPlayerEmoteEndEvent(Hook hook, Player player, @Nullable String emoteName) {
        super(hook, player);
        this.emoteName = emoteName;
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
}
