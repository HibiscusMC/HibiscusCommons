package me.lojosho.hibiscuscommons.nms;

import lombok.Getter;
import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class NMSHandlers {

    private static final LinkedHashMap<MinecraftVersion, MinecraftVersionInformation> VERSION_MAP = new LinkedHashMap <>() {{
        put(MinecraftVersion.v1_20_6, new MinecraftVersionInformation("v1_20_R4", true));
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
        String minecraftVersion = bukkitVersion.substring(0, bukkitVersion.indexOf('-'));
        MinecraftVersion enumVersion = MinecraftVersion.fromVersionString(minecraftVersion);
        MinecraftVersionInformation packageVersion = VERSION_MAP.get(enumVersion);

        if (packageVersion == null) {
            HibiscusCommonsPlugin.getInstance().getLogger().severe("An error occurred while trying to detect the version of the server.");
            HibiscusCommonsPlugin.getInstance().getLogger().severe(" ");
            HibiscusCommonsPlugin.getInstance().getLogger().severe("Detected Bukkit Version: " + bukkitVersion);
            HibiscusCommonsPlugin.getInstance().getLogger().severe("Detected Minecraft Version: " + minecraftVersion);
            HibiscusCommonsPlugin.getInstance().getLogger().severe("Detected Package Version: " + packageVersion);
            HibiscusCommonsPlugin.getInstance().getLogger().severe(" ");
            HibiscusCommonsPlugin.getInstance().getLogger().severe("Supported versions:");
            sendSupportedVersions();
            HibiscusCommonsPlugin.getInstance().getLogger().severe(" ");
            HibiscusCommonsPlugin.getInstance().getLogger().severe("Please update HibiscusCommons that supports this version.");
            throw new RuntimeException("Failed to detect the server version.");
        }

        for (Map.Entry<MinecraftVersion, MinecraftVersionInformation> selectedVersion : VERSION_MAP.entrySet()) {
            String internalReference = selectedVersion.getValue().internalReference();
            if (!internalReference.contains(packageVersion.internalReference())) {
                continue;
            }

            version = selectedVersion.getKey();

            if (!packageVersion.supported()) {
                HibiscusCommonsPlugin.getInstance().getLogger().severe("Detected Deprecated Version!");
                HibiscusCommonsPlugin.getInstance().getLogger().severe(" ");
                HibiscusCommonsPlugin.getInstance().getLogger().severe("Detected Bukkit Version: " + bukkitVersion);
                HibiscusCommonsPlugin.getInstance().getLogger().severe("Detected Minecraft Version: " + minecraftVersion);
                HibiscusCommonsPlugin.getInstance().getLogger().severe("Package Version: " + packageVersion.internalReference());
                HibiscusCommonsPlugin.getInstance().getLogger().severe("Is Supported: " + packageVersion.supported());
                HibiscusCommonsPlugin.getInstance().getLogger().severe(" ");
                HibiscusCommonsPlugin.getInstance().getLogger().severe("This version has no explicit support for it. There maybe errors that are unfixable.");
                HibiscusCommonsPlugin.getInstance().getLogger().severe("Consider moving to a version with explicit support. ");
                HibiscusCommonsPlugin.getInstance().getLogger().severe(" ");
                HibiscusCommonsPlugin.getInstance().getLogger().severe("Supported versions:");
                sendSupportedVersions();
                HibiscusCommonsPlugin.getInstance().getLogger().severe(" ");
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

    private record MinecraftVersionInformation(String internalReference, boolean supported) {}
}
