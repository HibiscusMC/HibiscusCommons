package me.lojosho.hibiscuscommons.nms;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface NMSUtils {

    int getNextEntityId();

    Entity getEntity(int entityId);

}
