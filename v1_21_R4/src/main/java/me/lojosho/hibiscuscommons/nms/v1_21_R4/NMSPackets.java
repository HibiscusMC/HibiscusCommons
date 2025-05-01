package me.lojosho.hibiscuscommons.nms.v1_21_R4;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import io.lumine.mythic.bukkit.utils.redis.jedis.Client;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntList;
import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.minecraft.advancements.*;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Constructor;
import java.util.*;

public class NMSPackets extends NMSCommon implements me.lojosho.hibiscuscommons.nms.NMSPackets {

    static Constructor<ClientboundSetPassengersPacket> passengerConstructor;

    static {
        try {
            passengerConstructor = ClientboundSetPassengersPacket.class.getDeclaredConstructor(FriendlyByteBuf.class);
            passengerConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendGamemodeChange(Player player, GameMode gameMode) {
        ClientboundGameEventPacket.Type type = ClientboundGameEventPacket.CHANGE_GAME_MODE;
        float param = gameMode.ordinal();

        ClientboundGameEventPacket packet = new ClientboundGameEventPacket(type, param);
        sendPacket(player, packet);
    }

    @Override
    public void sendRotateHeadPacket(int entityId, Location location, List<Player> sendTo) {
        ServerLevel level = MinecraftServer.getServer().overworld();
        Entity entity = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, level);
        entity.setId(entityId);
        byte headRot = (byte) (location.getYaw() * 256.0F / 360.0F);

        ClientboundRotateHeadPacket packet = new ClientboundRotateHeadPacket(entity, headRot);
        for (Player p : sendTo) sendPacket(p, packet);
    }

    @Override
    public void sendRotationPacket(int entityId, Location location, boolean onGround, List<Player> sendTo) {
        float ROTATION_FACTOR = 256.0F / 360.0F;
        byte yaw = (byte) (location.getYaw() * ROTATION_FACTOR);
        byte pitch = (byte) (location.getPitch() * ROTATION_FACTOR);
        ClientboundMoveEntityPacket.Rot packet = new ClientboundMoveEntityPacket.Rot(entityId, yaw, pitch, onGround);
        for (Player p : sendTo) sendPacket(p, packet);
    }

    @Override
    public void sendEquipmentSlotUpdate(
            int entityId,
            org.bukkit.inventory.EquipmentSlot slot,
            ItemStack item,
            List<Player> sendTo
    ) {

        EquipmentSlot nmsSlot = null;
        net.minecraft.world.item.ItemStack nmsItem = null;

        // Converting EquipmentSlot and ItemStack to NMS ones.
        nmsSlot = CraftEquipmentSlot.getNMS(slot);
        nmsItem = CraftItemStack.asNMSCopy(item);

        if (nmsSlot == null) return;

        Pair<EquipmentSlot, net.minecraft.world.item.ItemStack> pair = new Pair<>(nmsSlot, nmsItem);

        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> pairs = Collections.singletonList(pair);

        ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(entityId, pairs);
        for (Player p : sendTo) sendPacket(p, packet);
    }

    @Override
    public void sendEquipmentSlotUpdate(
            int entityId,
            HashMap<org.bukkit.inventory.EquipmentSlot, ItemStack> equipment,
            List<Player> sendTo
    ) {

        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> pairs = new ArrayList<>();

        for (org.bukkit.inventory.EquipmentSlot slot : equipment.keySet()) {
            EquipmentSlot nmsSlot = CraftEquipmentSlot.getNMS(slot);
            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(equipment.get(slot));

            Pair<EquipmentSlot, net.minecraft.world.item.ItemStack> pair = new Pair<>(nmsSlot, nmsItem);
            pairs.add(pair);
        }

        ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(entityId, pairs);
        for (Player p : sendTo) sendPacket(p, packet);
    }


    @Override
    public void sendSlotUpdate(
            Player player,
            int slot
    ) {
        int index = 0;

        ServerPlayer player1 = ((CraftPlayer) player).getHandle();

        if (index < Inventory.getSelectionSize()) {
            index += 36;
        } else if (index > 39) {
            index += 5; // Off hand
        } else if (index > 35) {
            index = 8 - (index - 36);
        }
        ItemStack item = player.getInventory().getItem(slot);

        Packet packet = new ClientboundContainerSetSlotPacket(player1.inventoryMenu.containerId, player1.inventoryMenu.incrementStateId(), index, CraftItemStack.asNMSCopy(item));
        sendPacket(player, packet);
    }

    @Override
    public void sendScoreboardHideNamePacket(Player player, String name) {
        //Creating the team
        PlayerTeam team = new PlayerTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), name);

        //Setting name visibility
        team.setNameTagVisibility(Team.Visibility.NEVER);

        //Remove the Team (i assume so if it exists)
        ClientboundSetPlayerTeamPacket removeTeamPacket = ClientboundSetPlayerTeamPacket.createRemovePacket(team);
        sendPacket(player, removeTeamPacket);
        //Creating the Team
        ClientboundSetPlayerTeamPacket createTeamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
        sendPacket(player, createTeamPacket);
        //Adding players to the team (You have to use the NPC's name, and add it to a list)
        ClientboundSetPlayerTeamPacket createPlayerTeamPacket = ClientboundSetPlayerTeamPacket.createMultiplePlayerPacket(team, new ArrayList<String>() {{
            add(name);
        }}, ClientboundSetPlayerTeamPacket.Action.ADD);
        sendPacket(player, createPlayerTeamPacket);
    }


    @Override
    public void sendMountPacket(int mountId, int[] passengerIds, List<Player> sendTo) {
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        byteBuf.writeVarInt(mountId);
        byteBuf.writeVarIntArray(passengerIds);
        try {
            ClientboundSetPassengersPacket packet = passengerConstructor.newInstance(byteBuf);
            for (Player p : sendTo) sendPacket(p, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLeashPacket(int leashEntity, int entityId, List<Player> sendTo) {
        // Fake entities just to avoid reflection
        ServerLevel level = MinecraftServer.getServer().overworld();
        Entity entity1 = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, level);
        Entity entity2 = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, level);
        entity1.setId(leashEntity);
        entity2.setId(entityId);

        ClientboundSetEntityLinkPacket packet = new ClientboundSetEntityLinkPacket(entity1, entity2);
        for (Player p : sendTo) sendPacket(p, packet);
    }

    @Override
    public void sendTeleportPacket(
            int entityId,
            double x,
            double y,
            double z,
            float yaw,
            float pitch,
            boolean onGround,
            List<Player> sendTo
    ) {
        try {
            ClientboundTeleportEntityPacket packet = ClientboundTeleportEntityPacket.teleport(entityId, new PositionMoveRotation(new Vec3(x, y, z), Vec3.ZERO, yaw, pitch), java.util.Set.of(), onGround);
            for (Player p : sendTo) sendPacket(p, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCameraPacket(int entityId, List<Player> sendTo) {
        // Fake entity just to avoid reflection
        Entity entity = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, MinecraftServer.getServer().overworld());
        entity.setId(entityId);

        ClientboundSetCameraPacket packet = new ClientboundSetCameraPacket(entity);
        for (Player p : sendTo) sendPacket(p, packet);
    }


    @Override
    public void sendSpawnEntityPacket(int entityId, UUID uuid, EntityType entityType, Location location, List<Player> sendTo) {
        net.minecraft.world.entity.EntityType<?> nmsEntityType = CraftEntityType.bukkitToMinecraft(entityType);
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        Vec3 velocity = Vec3.ZERO;
        float headYaw = 0f;

        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(entityId, uuid, x, y, z, yaw, pitch, nmsEntityType, 0, velocity, headYaw);
        for (Player p : sendTo) sendPacket(p, packet);
    }

    @Override
    public void sendEntityDestroyPacket(IntList entityIds, List<Player> sendTo) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(entityIds);
        for (Player p : sendTo) sendPacket(p, packet);
    }

    @Override
    public void sendItemDisplayMetadata(int entityId,
                                        Vector3f translation,
                                        Vector3f scale,
                                        Quaternionf rotationLeft,
                                        Quaternionf rotationRight,
                                        Display.Billboard billboard,
                                        int blockLight, int skyLight, float viewRange, float width, float height,
                                        ItemDisplay.ItemDisplayTransform transform, ItemStack itemStack,
                                        List<Player> sendTo) {

        List<SynchedEntityData.DataValue<?>> dataValues = new ArrayList<>();
        dataValues.add(new SynchedEntityData.DataValue<>(10, EntityDataSerializers.INT, POSITION_INTERPOLATION_DURATION));
        dataValues.add(new SynchedEntityData.DataValue<>(11, EntityDataSerializers.VECTOR3, translation));
        dataValues.add(new SynchedEntityData.DataValue<>(12, EntityDataSerializers.VECTOR3, scale));
        dataValues.add(new SynchedEntityData.DataValue<>(13, EntityDataSerializers.QUATERNION, rotationLeft));
        dataValues.add(new SynchedEntityData.DataValue<>(14, EntityDataSerializers.QUATERNION, rotationRight));
        dataValues.add(new SynchedEntityData.DataValue<>(15, EntityDataSerializers.BYTE, (byte) billboard.ordinal()));
        dataValues.add(new SynchedEntityData.DataValue<>(16, EntityDataSerializers.INT, (blockLight << 4 | skyLight << 20)));
        dataValues.add(new SynchedEntityData.DataValue<>(17, EntityDataSerializers.FLOAT, viewRange));
        dataValues.add(new SynchedEntityData.DataValue<>(20, EntityDataSerializers.FLOAT, width));
        dataValues.add(new SynchedEntityData.DataValue<>(21, EntityDataSerializers.FLOAT, height));
        dataValues.add(new SynchedEntityData.DataValue<>(23, EntityDataSerializers.ITEM_STACK, CraftItemStack.asNMSCopy(itemStack)));
        dataValues.add(new SynchedEntityData.DataValue<>(24, EntityDataSerializers.BYTE, (byte) transform.ordinal()));

        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(entityId, dataValues);
        for (Player p : sendTo) sendPacket(p, packet);
    }

    public void sendToastPacket(Player player, ItemStack icon, Component title, Component description) {
        final var key = ResourceLocation.fromNamespaceAndPath("hibiscuscommons", UUID.randomUUID().toString());

        JsonObject json = new JsonObject();

        // Creating the "criteria" object
        JsonObject impossibleCriteria = new JsonObject();
        JsonObject impossible = new JsonObject();
        impossible.addProperty("trigger", "minecraft:impossible");
        impossibleCriteria.add("impossible", impossible);
        json.add("criteria", impossibleCriteria);

        // Creating the "display" object
        JsonObject display = new JsonObject();
        JsonObject iconObj = new JsonObject();
        iconObj.addProperty("id", icon.getType().getKey().toString());

        if (icon.hasItemMeta()) {
            ItemMeta meta = icon.getItemMeta();
            JsonObject components = new JsonObject();

            if (!meta.getEnchants().isEmpty()) {
                components.addProperty("minecraft:enchantment_glint_override", true);
            }

            if (meta.hasCustomModelData()) {
                CustomModelDataComponent customModelDataComponent = meta.getCustomModelDataComponent();
                JsonObject customModelDataComponentJson = new JsonObject();

                List<Float> floats = customModelDataComponent.getFloats();
                if (!floats.isEmpty()) {
                    JsonArray floatsArray = new JsonArray();
                    floats.forEach(floatsArray::add);
                    customModelDataComponentJson.add("floats", floatsArray);
                }

                List<Boolean> flags = customModelDataComponent.getFlags();
                if (!flags.isEmpty()) {
                    JsonArray flagsArray = new JsonArray();
                    flags.forEach(flagsArray::add);
                    customModelDataComponentJson.add("flags", flagsArray);
                }

                List<String> strings = customModelDataComponent.getStrings();
                if (!strings.isEmpty()) {
                    JsonArray stringsArray = new JsonArray();
                    strings.forEach(stringsArray::add);
                    customModelDataComponentJson.add("strings", stringsArray);
                }

                List<Color> colors = customModelDataComponent.getColors();
                if (!colors.isEmpty()) {
                    JsonArray colorsArray = new JsonArray();
                    colors.forEach(color -> colorsArray.add(color.asRGB()));
                    customModelDataComponentJson.add("colors", colorsArray);
                }

                components.add("minecraft:custom_model_data", customModelDataComponentJson);
            }

            iconObj.add("components", components);
        }

        display.add("icon", iconObj);
        display.add("title", GsonComponentSerializer.gson().serializeToTree(title));
        display.add("description", GsonComponentSerializer.gson().serializeToTree(description));
        display.addProperty("description", "Toast Description");
        display.addProperty("frame", "task");
        display.addProperty("announce_to_chat", false);
        display.addProperty("show_toast", true);
        display.addProperty("hidden", true);

        json.add("display", display);

        final var advancement = Advancement.CODEC.parse(MinecraftServer.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE), json);
        final var advancementHolder = new AdvancementHolder(key, advancement.result().orElseThrow());

        final var nmsPlayer = ((CraftPlayer) player).getHandle();
        final var progress = nmsPlayer.getAdvancements().getOrStartProgress(advancementHolder);
        MinecraftServer.getServer().getAdvancements().tree().addAll(Set.of(advancementHolder));
        progress.getRemainingCriteria().forEach(criteria -> nmsPlayer.getAdvancements().award(advancementHolder, criteria));

        Bukkit.getScheduler().runTaskLater(HibiscusCommonsPlugin.getInstance(), () -> {
            progress.getRemainingCriteria().forEach(criteria -> nmsPlayer.getAdvancements().revoke(advancementHolder, criteria));
            MinecraftServer.getServer().getAdvancements().tree().remove(Set.of(key));

            // Remove the advancement from the player's client to prevent it from being displayed again
            // Was not working without this?
            ClientboundUpdateAdvancementsPacket removePacket = new ClientboundUpdateAdvancementsPacket(
                    false,
                    Collections.emptyList(),
                    Set.of(key),
                    Map.of(),
                    false
            );

            sendPacket(player, removePacket);
        }, 2L);
    }
}
