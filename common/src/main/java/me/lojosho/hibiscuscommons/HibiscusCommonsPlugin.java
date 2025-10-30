package me.lojosho.hibiscuscommons;

import lombok.Getter;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import me.lojosho.hibiscuscommons.listener.PlayerConnectionEvent;
import me.lojosho.hibiscuscommons.nms.NMSHandlers;
import me.lojosho.hibiscuscommons.task.FoliaTaskService;
import me.lojosho.hibiscuscommons.util.ServerUtils;
import org.jetbrains.annotations.ApiStatus;

public final class HibiscusCommonsPlugin extends HibiscusPlugin
{

    @Getter
    private static HibiscusCommonsPlugin instance;
    @Getter
    private static boolean onPaper = false;
    @Getter
    private static boolean onFolia = false;

    @Getter
    private static FoliaTaskService foliaTask;

    public HibiscusCommonsPlugin()
    {
        super(20726);
    }

    @Override
    public void onStart()
    {
        instance = this;

        // Do startup checks
        onPaper = checkPaper();
        onFolia = checkFolia();

        if (onFolia) {
            getLogger().info("Detected Folia by Cássio Martim! Enabling Folia support...");

            foliaTask = new FoliaTaskService(this);
            onPaper = false; // Just confirming that onPaper will not be true if Folia is true
        } else if (onPaper) {
            getLogger().info("Detected Paper! Enabling Paper support...");

            onFolia = false;
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

    @Override
    public void onEnd()
    {
        super.onEnd();

        if (onFolia && foliaTask != null)
            foliaTask.cancelTasks();
    }

    /**
     * Checks for Paper classes. Use {@link HibiscusCommonsPlugin#isOnPaper()} for cached value
     *
     * @return True if plugin is running on a server with Paper; False if not
     */
    @ApiStatus.Internal
    public boolean checkPaper()
    {
        return ServerUtils.hasClass("com.destroystokyo.paper.PaperConfig") || ServerUtils.hasClass("io.papermc.paper.configuration.Configuration");
    }

    /**
     * Checks for the Folia classes. Use {@link HibiscusCommonsPlugin#isOnFolia()} for cached value.
     *
     * @return True if plugin is running on a server with Folia; False if not
     */
    @ApiStatus.Internal
    public boolean checkFolia()
    {
        return ServerUtils.hasClass("io.papermc.paper.threadedregions.RegionizedServer");
    }
}
