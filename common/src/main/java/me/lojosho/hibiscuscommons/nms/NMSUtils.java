package me.lojosho.hibiscuscommons.nms;

import org.bukkit.entity.Entity;

public interface NMSUtils {

    int getNextEntityId();

    Entity getEntity(int entityId);

}
