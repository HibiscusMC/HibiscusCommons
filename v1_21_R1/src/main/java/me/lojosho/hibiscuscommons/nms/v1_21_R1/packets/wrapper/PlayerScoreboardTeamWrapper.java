package me.lojosho.hibiscuscommons.nms.v1_21_R1.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import org.bukkit.entity.Player;

public abstract class PlayerScoreboardTeamWrapper implements PacketWrapper {

    protected final Player player;
    protected final String name;

    public PlayerScoreboardTeamWrapper(Player player, String name) {
        this.player = player;
        this.name = name;
    }
}
