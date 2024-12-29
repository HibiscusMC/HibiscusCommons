package me.lojosho.hibiscuscommons;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class HibiscusPlugin extends JavaPlugin {

    @Getter
    private final int bstats;
    @Getter
    private final int resourceID;
    @Getter
    private String latestVersion = "";
    @Getter
    private boolean onLatestVersion = true;
    @Getter
    private boolean disabled = false;

    protected HibiscusPlugin() {
        this(-1);
    }

    protected HibiscusPlugin(int bstats) {
        this(bstats, -1);
    }

    protected HibiscusPlugin(int bstats, int resourceID) {
        this.bstats = bstats;
        this.resourceID = resourceID;
    }

    @Override
    public final void onEnable() {
        super.onEnable();

        Plugin hibiscusCommons = Bukkit.getPluginManager().getPlugin("HibiscusCommons");
        if (hibiscusCommons == null || !hibiscusCommons.isEnabled()) {
            getLogger().severe("");
            getLogger().severe("HibiscusCommons is required to be enabled to run this plugin!");
            getLogger().severe("");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (bstats > 0) {
            Metrics metrics = new Metrics(this, bstats);
        }
        if (resourceID > 0) {
            setupResourceUpdateChecker(this, resourceID);
        }

        onStart();
    }

    private void setupResourceUpdateChecker(HibiscusPlugin plugin, int resourceID) {
        // Update Checker
        UpdateChecker checker = new UpdateChecker(plugin, UpdateCheckSource.POLYMART, String.valueOf(resourceID))
                .onSuccess((commandSenders, latestVersion) -> {
                    this.latestVersion = (String) latestVersion;
                    String pluginName = getDescription().getName();

                    if (!this.latestVersion.equalsIgnoreCase(getDescription().getVersion())) {
                        getLogger().info("+++++++++++++++++++++++++++++++++++");
                        getLogger().info("There is a new update for " + pluginName + "!");
                        getLogger().info("Please download it as soon as possible for possible fixes and new features.");
                        getLogger().info("Current Version " + getDescription().getVersion() + " | Latest Version " + latestVersion);
                        //getLogger().info("Spigot: https://www.spigotmc.org/resources/100107/");
                        getLogger().info("Polymart: https://polymart.org/resource/" + resourceID);
                        getLogger().info("+++++++++++++++++++++++++++++++++++");
                    } else {
                        getLogger().info("You are running the latest version of " + pluginName + "!");
                    }
                })
                .setNotifyRequesters(false)
                .setNotifyOpsOnJoin(false)
                .checkEveryXHours(24)
                .checkNow();
        onLatestVersion = checker.isUsingLatestVersion();
    }


    @Override
    public final void onDisable() {
        disabled = true;

        onEnd();
    }


    public void onStart() {
        // Override
    }

    public void onReload() {
        // Override
    }

    public void onEnd() {
        // Override
    }

    public HibiscusPlugin get() {
        return this;
    }

}
