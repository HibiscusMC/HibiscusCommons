package me.lojosho.hibiscuscommons.nms.v1_21_R5.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;

public class PlayerScoreboardTeamRemoveWrapper extends PlayerScoreboardTeamWrapper {

    public PlayerScoreboardTeamRemoveWrapper(Player player, String name) {
        super(player, name);
    }

    @Override
    public PacketType getType() {
        return PacketType.PLAYER_SCOREBOARD_HIDE_USERNAME;
    }

    @Override
    public Object toNativePacket() {
        //Creating the team
        PlayerTeam team = new PlayerTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), name);

        //Setting name visibility
        team.setNameTagVisibility(Team.Visibility.NEVER);

        //Remove the Team (i assume so if it exists)
        return ClientboundSetPlayerTeamPacket.createRemovePacket(team);
    }
}
