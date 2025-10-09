package me.lojosho.hibiscuscommons.util;

import org.jetbrains.annotations.NotNull;

public class StringUtils {

    @NotNull
    public static String parseStringToString(final String parsed) {
        return AdventureUtils.MINI_MESSAGE.serialize(AdventureUtils.MINI_MESSAGE.deserialize(parsed));
    }

}
