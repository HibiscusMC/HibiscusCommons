package me.lojosho.hibiscuscommons.packets.wrapper;

import me.lojosho.hibiscuscommons.nms.NMSHandlers;
import me.lojosho.hibiscuscommons.packets.PacketType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PacketWrapper {

    PacketType getType();
    Object toNativePacket();

    default void sendPacket(@NotNull Player... player) {
        NMSHandlers.getHandler().getPacketSender().sendPacket(this, player);
    }

    default void sendPacket(@NotNull List<Player> players) {
        NMSHandlers.getHandler().getPacketSender().sendPacket(this, players);
    }

}
