package me.lojosho.hibiscuscommons.nms.v1_21_R6.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerScoreboardTeamAddPlayersWrapper extends PlayerScoreboardTeamWrapper {

    public PlayerScoreboardTeamAddPlayersWrapper(Player player, String name) {
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

        //Adding players to the team (You have to use the NPC's name, and add it to a list)
        return ClientboundSetPlayerTeamPacket.createMultiplePlayerPacket(team, new ArrayList<String>() {{
            add(name);
            add(player.getName());
        }}, ClientboundSetPlayerTeamPacket.Action.ADD);
    }
}
