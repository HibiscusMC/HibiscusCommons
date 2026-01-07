package me.lojosho.hibiscuscommons.nms.v1_21_R6.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.attribute.CraftAttribute;

import java.util.List;

public class EntityAttributeWrapper implements PacketWrapper {

    private final int entityId;
    private final Attribute bukkitAttribute;
    private final double value;

    public EntityAttributeWrapper(int entityId, Attribute attribute, double value) {
        this.entityId = entityId;
        this.bukkitAttribute = attribute;
        this.value = value;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_ATTRIBUTE_UPDATE;
    }

    @Override
    public Object toNativePacket() {
        AttributeInstance attribute = new AttributeInstance(
                CraftAttribute.bukkitToMinecraftHolder(bukkitAttribute),
                (ignored) -> {}
        );
        attribute.setBaseValue(value);

        return new ClientboundUpdateAttributesPacket(entityId, List.of(attribute));
    }
}
