package me.lojosho.hibiscuscommons.nms;

import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface NMSPacketSender {

    void sendPacket(@NotNull PacketWrapper wrapper, @NotNull Player... players);
    void sendPacket(@NotNull PacketWrapper wrapper, @NotNull List<Player> players);
    void sendBundle(@NotNull List<PacketWrapper> wrappers, @NotNull Player... players);
    void sendBundle(@NotNull List<PacketWrapper> wrappers, @NotNull List<Player> players);

}
