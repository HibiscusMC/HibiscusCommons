package me.lojosho.hibiscuscommons.nms;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class NMSHandler {

    private static NMSHandler instance;
    @Getter
    private NMSUtils utilHandler;
    /**
     * @deprecated Move to {@link me.lojosho.hibiscuscommons.util.packets.PacketManager}
     */
    @Getter
    @Deprecated
    private NMSPackets packetHandler;

    public NMSHandler(NMSUtils utilHandler, NMSPackets packetHandler) {
        if (instance != null) {
            throw new IllegalStateException("NMSHandler is already initialized.");
        }
        this.utilHandler = utilHandler;
        this.packetHandler = packetHandler;

        instance = this;
    }

    public Entity getEntity(int entityId) {
        for (World world : Bukkit.getWorlds()) {
            Entity entity = SpigotConversionUtil.getEntityById(world, entityId);
            if (entity != null) {
                return entity;
            }
        }

        return null;
    }
}
