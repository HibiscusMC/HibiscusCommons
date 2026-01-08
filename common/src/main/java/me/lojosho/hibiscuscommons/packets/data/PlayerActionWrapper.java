package me.lojosho.hibiscuscommons.packets.data;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class PlayerActionWrapper {

    @Getter
    private final String actionType;

    public PlayerActionWrapper(@NotNull String actionType) {
        this.actionType = actionType;
    }
}
