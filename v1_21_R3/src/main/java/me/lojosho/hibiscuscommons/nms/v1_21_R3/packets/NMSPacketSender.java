package me.lojosho.hibiscuscommons.nms.v1_21_R3.packets;

import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class NMSPacketSender implements me.lojosho.hibiscuscommons.nms.NMSPacketSender {

    @Override
    public void sendPacket(@NotNull PacketWrapper wrapper, @NotNull Player... players) {
        final Packet<?> packet = (Packet<?>) wrapper.toNativePacket();
        for (Player player : players) sendPacketToPlayer(player, packet);
    }

    @Override
    public void sendPacket(@NonNull PacketWrapper wrapper, @NonNull List<Player> players) {
        final Packet<?> packet = (Packet<?>) wrapper.toNativePacket();
        for (Player player : players) sendPacketToPlayer(player, packet);
    }

    @Override
    public void sendBundle(@NotNull List<PacketWrapper> wrappers, @NotNull Player... players) {
        // Convert wrappers to native packets with proper generic type
        final List<Packet<? super ClientGamePacketListener>> packets = wrappers.stream()
                .map(wrapper -> castToClientPacket(wrapper.toNativePacket()))
                .collect(Collectors.toList());

        // Create bundle packet with Iterable
        final ClientboundBundlePacket bundlePacket = new ClientboundBundlePacket(packets);
        for (Player player : players) sendPacketToPlayer(player, bundlePacket);
    }

    @Override
    public void sendBundle(@NonNull List<PacketWrapper> wrappers, @NonNull List<Player> players) {
        // Convert wrappers to native packets with proper generic type
        final List<Packet<? super ClientGamePacketListener>> packets = wrappers.stream()
                .map(wrapper -> castToClientPacket(wrapper.toNativePacket()))
                .collect(Collectors.toList());

        // Create bundle packet with Iterable
        final ClientboundBundlePacket bundlePacket = new ClientboundBundlePacket(packets);
        for (Player player : players) sendPacketToPlayer(player, bundlePacket);
    }

    @SuppressWarnings("unchecked")
    private Packet<? super ClientGamePacketListener> castToClientPacket(Object packet) {
        return (Packet<? super ClientGamePacketListener>) packet;
    }

    private void sendPacketToPlayer(Player player, Packet<?> packet) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        ServerPlayerConnection connection = nmsPlayer.connection;
        connection.send(packet);
    }
}
