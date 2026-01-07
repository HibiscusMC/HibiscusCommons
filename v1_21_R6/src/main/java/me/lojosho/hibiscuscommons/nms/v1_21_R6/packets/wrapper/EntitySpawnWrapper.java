package me.lojosho.hibiscuscommons.nms.v1_21_R6.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class EntitySpawnWrapper implements PacketWrapper {

    private final int entityId;
    private final EntityType entityType;
    private final UUID uuid;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public EntitySpawnWrapper(
            int entityId,
            EntityType entityType,
            UUID uuid,
            double x,
            double y,
            double z,
            float yaw,
            float pitch
    ) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_SPAWN;
    }

    @Override
    public Object toNativePacket() {
        net.minecraft.world.entity.EntityType<?> nmsEntityType = CraftEntityType.bukkitToMinecraft(entityType);
        Vec3 velocity = Vec3.ZERO;
        float headYaw = 0f;

        return new ClientboundAddEntityPacket(entityId, uuid, x, y, z, yaw, pitch, nmsEntityType, 0, velocity, headYaw);
    }
}
