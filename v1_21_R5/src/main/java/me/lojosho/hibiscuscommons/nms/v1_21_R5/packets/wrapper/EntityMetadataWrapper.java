package me.lojosho.hibiscuscommons.nms.v1_21_R5.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityMetadataWrapper implements PacketWrapper {

    private final int entityId;
    private final Map<Integer, Number> dataValues;

    public EntityMetadataWrapper(int entityId, Map<Integer, Number> dataValues) {
        this.entityId = entityId;
        this.dataValues = dataValues;
    }

    @Override
    public PacketType getType() {
        return PacketType.ENTITY_METADATA;
    }

    @Override
    public Object toNativePacket() {
        List<SynchedEntityData.DataValue<?>> nmsDataValues = dataValues.entrySet().stream().map(entry -> {
            int index = entry.getKey();
            Number value = entry.getValue();
            return switch (value) {
                case Byte byteVal -> new SynchedEntityData.DataValue<>(index, EntityDataSerializers.BYTE, byteVal);
                case Float floatVal -> new SynchedEntityData.DataValue<>(index, EntityDataSerializers.FLOAT, floatVal);
                case Integer intVal -> new SynchedEntityData.DataValue<>(index, EntityDataSerializers.INT, intVal);
                default ->
                        throw new IllegalArgumentException("Unsupported data value type: " + value.getClass().getSimpleName());
            };
        }).collect(Collectors.toList());

        return new ClientboundSetEntityDataPacket(entityId, nmsDataValues);
    }
}
