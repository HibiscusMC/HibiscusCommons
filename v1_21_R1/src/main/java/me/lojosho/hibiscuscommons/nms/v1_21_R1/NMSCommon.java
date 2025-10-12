package me.lojosho.hibiscuscommons.nms.v1_21_R1;

import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NMSCommon {

    public void sendPacket(@NotNull Player player, @NotNull Packet packet) {
        Bukkit.getAsyncScheduler().runNow(HibiscusCommonsPlugin.getInstance(), (task) -> {
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            ServerPlayerConnection connection = serverPlayer.connection;
            connection.send(packet);
        });
    }

    public void sendPacket(@NotNull List<Player> players, @NotNull Packet packet) {
        Bukkit.getAsyncScheduler().runNow(HibiscusCommonsPlugin.getInstance(), (task) -> {
            for (Player p : players) {
                ServerPlayer serverPlayer = ((CraftPlayer) p).getHandle();
                ServerPlayerConnection connection = serverPlayer.connection;
                connection.send(packet);
            }
        });
    }
}
