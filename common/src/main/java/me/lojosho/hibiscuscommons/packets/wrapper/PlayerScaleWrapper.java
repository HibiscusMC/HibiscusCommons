package me.lojosho.hibiscuscommons.packets.wrapper;

import lombok.Getter;

public class PlayerScaleWrapper {

    @Getter
    private final int entityId;
    @Getter
    private final double scale;
    @Getter
    private final double base;

    public PlayerScaleWrapper(int entityId, double base, double scale) {
        this.entityId = entityId;
        this.scale = scale;
        this.base = base;
    }
}
