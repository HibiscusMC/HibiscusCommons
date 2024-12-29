package me.lojosho.hibiscuscommons;

import lombok.Getter;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import me.lojosho.hibiscuscommons.nms.NMSHandlers;
import me.lojosho.hibiscuscommons.util.ServerUtils;

public final class HibiscusCommonsPlugin extends HibiscusPlugin {

    @Getter
    private static HibiscusCommonsPlugin instance;
    @Getter
    private static boolean onPaper = false;

    public HibiscusCommonsPlugin() {
        super(20726);
    }

    @Override
    public void onStart() {
        instance = this;

        try {
            NMSHandlers.setup();
        } catch (RuntimeException e) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Detects if a user is running a paper server
        if (ServerUtils.hasClass("com.destroystokyo.paper.PaperConfig") || ServerUtils.hasClass("io.papermc.paper.configuration.Configuration")) {
            onPaper = true;
            getLogger().info("Detected Paper! Enabling Paper support...");
            //getServer().getPluginManager().registerEvents(new PaperPlayerGameListener(), this);
        }

        // Plugin startup logic
        Hooks.setup();

    }
}
