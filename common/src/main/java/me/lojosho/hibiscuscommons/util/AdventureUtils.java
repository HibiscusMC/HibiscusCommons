package me.lojosho.hibiscuscommons.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class AdventureUtils {

    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    public static final MiniMessage MINI_MESSAGE = MiniMessage.builder().tags(
                    StandardTags.defaults()
            ).
            build();

}
