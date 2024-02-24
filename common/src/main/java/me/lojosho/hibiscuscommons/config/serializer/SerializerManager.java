package me.lojosho.hibiscuscommons.config.serializer;

import me.lojosho.hibiscuscommons.items.ItemBuilder;
import org.bukkit.Location;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;


public class SerializerManager {

    /**
     * @deprecated Use {@link #getItemBuilderSerializer()} instead
     */
    @Deprecated (since = "0.2.7", forRemoval = true)
    public static ItemSerializer getItemSerializer() {
        return ItemSerializer.INSTANCE;
    }

    public static ItemBuilderSerializer getItemBuilderSerializer() {
        return ItemBuilderSerializer.INSTANCE;
    }

    public static ItemBuilder serializeItemBuilder(ConfigurationNode source) throws SerializationException {
        return ItemBuilderSerializer.INSTANCE.deserialize(ItemBuilder.class, source);
    }

    public static Location serializeLocation(ConfigurationNode source) throws SerializationException {
        return LocationSerializer.INSTANCE.deserialize(Location.class, source);
    }
}