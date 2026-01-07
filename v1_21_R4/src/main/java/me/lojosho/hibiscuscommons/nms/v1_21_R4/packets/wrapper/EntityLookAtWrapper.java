package me.lojosho.hibiscuscommons.nms.v1_21_R4.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class EntityLookAtWrapper implements PacketWrapper {

    private static final Entity fakeNmsEntity = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, MinecraftServer.getServer().overworld());

    private final int entityId;
    private final Location location;

    public EntityLookAtWrapper(int entityId, @NotNull Location location) {
        this.entityId = entityId;
        this.location = location;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_LOOK_AT;
    }

    @Override
    public Object toNativePacket() {
        fakeNmsEntity.setId(entityId);
        fakeNmsEntity.getBukkitEntity().teleport(location);
        return new ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor.EYES, fakeNmsEntity, EntityAnchorArgument.Anchor.EYES);
    }
}
