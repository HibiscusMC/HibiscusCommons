package me.lojosho.hibiscuscommons.nms;

import lombok.Getter;
import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class NMSHandlers {

    private static final LinkedHashMap<MinecraftVersion, MinecraftVersionInformation> VERSION_MAP = new LinkedHashMap <>() {{
        put(MinecraftVersion.v1_21, new MinecraftVersionInformation("v1_21_R1", false)); // 1.20 is not supported; was imminently bumped to 1.21.1
        put(MinecraftVersion.v1_21_1, new MinecraftVersionInformation("v1_21_R1", true));
        put(MinecraftVersion.v1_21_2, new MinecraftVersionInformation("v1_21_R2", false)); // 1.20.2 is not supported; was imminently bumped to 1.21.3
        put(MinecraftVersion.v1_21_3, new MinecraftVersionInformation("v1_21_R2", true));
        put(MinecraftVersion.v1_21_4, new MinecraftVersionInformation("v1_21_R3", true));
        put(MinecraftVersion.v1_21_5, new MinecraftVersionInformation("v1_21_R4", true));
        put(MinecraftVersion.v1_21_6, new MinecraftVersionInformation("v1_21_R5", false));
        put(MinecraftVersion.v1_21_7, new MinecraftVersionInformation("v1_21_R5", false));
        put(MinecraftVersion.v1_21_8, new MinecraftVersionInformation("v1_21_R5", true));
        put(MinecraftVersion.v1_21_9, new MinecraftVersionInformation("v1_21_R6", false));
        put(MinecraftVersion.v1_21_10, new MinecraftVersionInformation("v1_21_R6", true));
        put(MinecraftVersion.v1_21_11, new MinecraftVersionInformation("v1_21_R7", true));
        put(MinecraftVersion.v26_1_1, new MinecraftVersionInformation("v26_1_R1", false));
        put(MinecraftVersion.v26_1_2, new MinecraftVersionInformation("v26_1_R1", true));
    }};

    private static NMSHandler handler;
    @Getter
    private static MinecraftVersion version;

    public static boolean isVersionSupported() {
        return getVersion() != null;
    }

    public static NMSHandler getHandler() {
        if (handler == null) setup();
        return handler;
    }

    public static void setup() throws RuntimeException {
        if (handler != null) return;
        final String bukkitVersion = Bukkit.getServer().getBukkitVersion();
        String minecraftVersion = getMinecraftVersion(bukkitVersion);
        MinecraftVersion enumVersion = MinecraftVersion.fromVersionString(minecraftVersion);
        MinecraftVersionInformation packageVersion = VERSION_MAP.get(enumVersion);

        final Logger logger = HibiscusCommonsPlugin.getInstance().getLogger();

        if (packageVersion == null) {
            logger.severe("An error occurred while trying to detect the version of the server.");
            logger.severe(" ");
            logger.severe("Detected Bukkit Version: " + bukkitVersion);
            logger.severe("Detected Minecraft Version: " + minecraftVersion);
            logger.severe("Detected Package Version: " + packageVersion);
            logger.severe(" ");
            logger.severe("Supported versions:");
            sendSupportedVersions();
            logger.severe(" ");
            logger.severe("Please update HibiscusCommons that supports this version.");
            throw new RuntimeException("Failed to detect the server version.");
        }

        for (Map.Entry<MinecraftVersion, MinecraftVersionInformation> selectedVersion : VERSION_MAP.entrySet()) {
            String internalReference = selectedVersion.getValue().internalReference();
            if (!internalReference.contains(packageVersion.internalReference())) {
                continue;
            }

            version = selectedVersion.getKey();

            if (!packageVersion.supported()) {
                logger.severe("Detected Deprecated Version!");
                logger.severe(" ");
                logger.severe("Detected Bukkit Version: " + bukkitVersion);
                logger.severe("Detected Minecraft Version: " + minecraftVersion);
                logger.severe("Package Version: " + packageVersion.internalReference());
                logger.severe("Is Supported: " + packageVersion.supported());
                logger.severe(" ");
                logger.severe("This version has no explicit support for it. There maybe errors that are unfixable.");
                logger.severe("Consider moving to a version with explicit support. ");
                logger.severe(" ");
                logger.severe("Supported versions:");
                sendSupportedVersions();
                logger.severe(" ");
            }

            try {
                NMSUtils utilHandler = (NMSUtils) Class.forName("me.lojosho.hibiscuscommons.nms." + packageVersion.internalReference() + ".NMSUtils").getConstructor().newInstance();
                NMSPacketBuilder packetHandler = (NMSPacketBuilder) Class.forName("me.lojosho.hibiscuscommons.nms." + packageVersion.internalReference() + ".packets.NMSPackets").getConstructor().newInstance();
                NMSPacketSender packetSender = (NMSPacketSender) Class.forName("me.lojosho.hibiscuscommons.nms." + packageVersion.internalReference() + ".packets.NMSPacketSender").getConstructor().newInstance();
                handler = new NMSHandler(utilHandler, packetHandler, packetSender);
                return;
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                     IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void sendSupportedVersions() {
        for (Map.Entry<MinecraftVersion, MinecraftVersionInformation> entry : VERSION_MAP.entrySet()) {
            if (!entry.getValue().supported()) continue;
            HibiscusCommonsPlugin.getInstance().getLogger().severe("  - " + entry.getKey().toVersionString());
        }
    }

    private static String getMinecraftVersion(String bukkitVersion) {
        String minecraftVersion = bukkitVersion.substring(0, bukkitVersion.indexOf('-')); // Legacy-wise this is enough
        if (minecraftVersion.contains("build")) {
            // Paper new 26.1+ versioning system; Ex. 26.1.2.build.51-beta
            minecraftVersion = minecraftVersion.substring(0, minecraftVersion.indexOf(".build"));
        }
        return minecraftVersion;
    }

    private record MinecraftVersionInformation(String internalReference, boolean supported) {}
}
