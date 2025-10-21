package me.lojosho.hibiscuscommons.nms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MinecraftVersion {
    v1_20_4,
    v1_20_6,
    v1_21,
    v1_21_1,
    v1_21_2,
    v1_21_3,
    v1_21_4,
    v1_21_5,
    v1_21_6,
    v1_21_7,
    v1_21_8,
    v1_21_9,
    v1_21_10,
    ;

    public boolean isHigher(MinecraftVersion other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean isHigherOrEqual(MinecraftVersion other) {
        return this.ordinal() >= other.ordinal();
    }

    public boolean isLowerOrEqual(MinecraftVersion other) {
        return this.ordinal() <= other.ordinal();
    }

    public boolean isLower(MinecraftVersion other) {
        return this.ordinal() < other.ordinal();
    }

    /**
     * Converts the enum into a usable string.
     * @return Returns string of version (such as 1.21.4)
     */
    @NotNull
    public String toVersionString() {
        // Remove the "v" prefix and replace underscores with dots
        return name().substring(1).replace('_', '.');
    }

    /**
     * Returns the enum from a version. Returns null if invalid version
     * @param version A version number, such as 1.21.4
     * @return Returns the enum, such as v1_21_4
     */
    @Nullable
    public static MinecraftVersion fromVersionString(@NotNull String version) {
        String enumName = "v" + version.replace('.', '_');
        for (MinecraftVersion v : values()) {
            if (v.name().equals(enumName)) {
                return v;
            }
        }
        return null;
    }
}
