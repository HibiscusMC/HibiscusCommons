package me.lojosho.hibiscuscommons.util;

import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import me.lojosho.hibiscuscommons.nms.NMSHandlers;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerUtils {

    public static int getNextEntityId() {
        return NMSHandlers.getHandler().getUtilHandler().getNextEntityId();
    }

    @Nullable
    public static PlayerTextures getSkin(Player player) {
        return player.getPlayerProfile().getTextures();
    }

    /**
     * Parse a color from a string.
     * Formats: #RRGGBB; R,G,B
     *
     * @param color The string
     * @return The color, if the string can't be parsed, null is returned
     */
    public static Color colorFromString(@Nullable String color) {
        if (color == null) {
            return null;
        }
        try {
            var decodedColor = java.awt.Color.decode(color.startsWith("#") ? color : "#" + color);
            return Color.fromRGB(decodedColor.getRed(), decodedColor.getGreen(), decodedColor.getBlue());
        } catch (NumberFormatException invalidHex) {
            try {
                var rgbValues = Arrays.stream(color.split(",")).map(Integer::parseInt).toArray(Integer[]::new);
                return Color.fromRGB(rgbValues[0], rgbValues[1], rgbValues[2]);
            } catch (Exception invalidRgb) {
                return null;
            }
        }
    }

    /**
     * This takes in a string like #FFFFFF to convert it into a Bukkit color
     * @param colorStr
     * @return
     */
    public static Color hex2Rgb(String colorStr) {
        if (colorStr.startsWith("#")) return Color.fromRGB(Integer.valueOf(colorStr.substring(1), 16));
        if (colorStr.startsWith("0x")) return Color.fromRGB(Integer.valueOf(colorStr.substring(2), 16));
        if (colorStr.contains(",")) {
            String[] colorString = colorStr.replace(" ", "").split(",");
            for (String color : colorString) if (Integer.valueOf(color) == null) return Color.WHITE;
            Color.fromRGB(Integer.valueOf(colorString[0]), Integer.valueOf(colorString[1]), Integer.valueOf(colorString[2]));
        }

        return Color.WHITE;
    }

    /**
     * This takes in a string like 55,49,181 to convert it into a Bukkit Color
     * @param colorStr
     * @return
     */
    public static Color rgbToRgb(String colorStr) {
        if (colorStr.contains(",")) {
            String[] colors = colorStr.split(",", 3);
            if (colors.length == 3) {
                return Color.fromRGB(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
            }
        }

        return Color.WHITE;
    }

    public static int getNextYaw(final int current, final int rotationSpeed) {
        int nextYaw = current + rotationSpeed;
        if (nextYaw > 179) {
            nextYaw = (current + rotationSpeed) - 358;
            return nextYaw;
        }
        return nextYaw;
    }

    /**
     * Get the viewers of an entity (Players only) that can see the entity in the world.
     * This ignores config view distances and checks directly with the server.
     * @param entity
     * @return
     */
    @NotNull
    public static List<Player> getViewers(@NotNull Entity entity) {
        if (HibiscusCommonsPlugin.isOnPaper()) return List.copyOf(entity.getTrackedBy());
        ArrayList<Player> viewers = new ArrayList<>();
        for (Player player : entity.getLocation().getWorld().getPlayers()) {
            if (player.canSee(entity)) viewers.add(player);
        }
        return viewers;
    }

    public static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
