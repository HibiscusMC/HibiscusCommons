package me.lojosho.hibiscuscommons.packets.data;

import lombok.Getter;

public class PlayerInteractWrapper {

    @Getter
    private final int entityId;

    public PlayerInteractWrapper(int entityId) {
        this.entityId = entityId;
    }
}
