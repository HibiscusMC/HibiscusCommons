package me.lojosho.hibiscuscommons.nms.v1_21_R7.packets.wrapper;

import me.lojosho.hibiscuscommons.nms.v1_21_R7.packets.wrapper.PlayerScoreboardTeamWrapper;
import me.lojosho.hibiscuscommons.packets.PacketType;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;

public class PlayerScoreboardTeamCreateWrapper extends PlayerScoreboardTeamWrapper {

    public PlayerScoreboardTeamCreateWrapper(Player player, String name) {
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

        //Creating the Team
        return ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
    }
}
