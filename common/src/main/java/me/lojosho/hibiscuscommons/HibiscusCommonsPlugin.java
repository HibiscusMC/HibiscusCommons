package me.lojosho.hibiscuscommons;

import lombok.Getter;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import me.lojosho.hibiscuscommons.listener.PlayerConnectionEvent;
import me.lojosho.hibiscuscommons.nms.NMSHandlers;
import me.lojosho.hibiscuscommons.util.ServerUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

public final class HibiscusCommonsPlugin extends HibiscusPlugin {

    @Getter
    private static HibiscusCommonsPlugin instance;
    @Getter
    private static boolean onPaper = false;
    @Getter
    private static boolean onFolia = false;

    public HibiscusCommonsPlugin() {
        super(20726);
    }

    @Override
    public void onStart() {
        instance = this;

        // Do startup checks
        onPaper = checkPaper();
        onFolia = checkFolia();
        if (onPaper) {
            getLogger().info("Detected Paper! Enabling Paper support...");
            //getServer().getPluginManager().registerEvents(new PaperPlayerGameListener(), this);
        } else {
            getLogger().warning("Paper was not detected! Some features may not work as expected.");
            getLogger().warning("Please consider using Paper for the best experience.");
            getLogger().warning("Download Paper at: https://papermc.io/");
        }

        try {
            NMSHandlers.setup();
        } catch (RuntimeException e) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new PlayerConnectionEvent(), this);

        // Plugin startup logic
        Hooks.setup();
    }

    /**
     * Checks for Paper classes. Use {@link HibiscusCommonsPlugin#isOnPaper()} for cached value
     * @return True if plugin is running on a server with Paper; False if not
     */
    @ApiStatus.Internal
    public boolean checkPaper() {
        if (ServerUtils.hasClass("com.destroystokyo.paper.PaperConfig") || ServerUtils.hasClass("io.papermc.paper.configuration.Configuration")) {
            return true;
        }
        return false;
    }

    /**
     * Checks for the Folia region scheduler method. Use {@link HibiscusCommonsPlugin#isOnFolia()} for cached value.
     * @return True if plugin is running on a server with Folia; False if not
     */
    @ApiStatus.Internal
    public boolean checkFolia() {
        try {
            Bukkit.class.getMethod("getRegionScheduler");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
