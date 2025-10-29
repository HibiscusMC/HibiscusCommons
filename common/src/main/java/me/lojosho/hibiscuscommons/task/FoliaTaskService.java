package me.lojosho.hibiscuscommons.task;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class FoliaTaskService
{

    private final Plugin plugin;

    private final AtomicInteger idCounter = new AtomicInteger(1);
    private final Map<Integer, ScheduledTask> tasks = new ConcurrentHashMap<>();

    private final AsyncScheduler asyncScheduler = Bukkit.getAsyncScheduler();
    private final GlobalRegionScheduler globalScheduler = Bukkit.getGlobalRegionScheduler();

    private int register(ScheduledTask task)
    {
        int id = idCounter.getAndIncrement();
        tasks.put(id, task);
        return id;
    }

    public void cancelTasks()
    {
        tasks.values().forEach(ScheduledTask::cancel);
        tasks.clear();
    }

    public int scheduleAsyncRepeatingTask(Runnable runnable, int delayUntilFirst, int tickInterval)
    {
        long delayMillis = ticksToMillis(delayUntilFirst);
        long periodMillis = ticksToMillis(tickInterval);

        ScheduledTask task = asyncScheduler.runAtFixedRate(plugin,
                scheduledTask -> runnable.run(),
                delayMillis, periodMillis, TimeUnit.MILLISECONDS);

        return register(task);
    }

    public int scheduleSyncRepeatingTask(Runnable runnable, int delayUntilFirst, int tickInterval)
    {
        ScheduledTask task = globalScheduler.runAtFixedRate(plugin,
                scheduledTask -> runnable.run(),
                delayUntilFirst, tickInterval);

        return register(task);
    }

    public int scheduleSyncDelayedTask(Runnable runnable, int delay)
    {
        ScheduledTask task = globalScheduler.runDelayed(plugin,
                scheduledTask -> runnable.run(),
                (long) delay);

        return register(task);
    }

    public void cancelRepeatingTask(int id)
    {
        ScheduledTask task = tasks.remove(id);
        if (task != null) {
            task.cancel();
        }
    }

    public void runAsync(Runnable runnable)
    {
        asyncScheduler.runNow(plugin, scheduledTask -> runnable.run());
    }

    public void runSync(Runnable runnable)
    {
        globalScheduler.execute(plugin, runnable);
    }

    private long ticksToMillis(int ticks)
    {
        return (long) ticks * 50L;
    }
}