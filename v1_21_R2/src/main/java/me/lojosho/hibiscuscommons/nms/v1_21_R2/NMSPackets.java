package me.lojosho.hibiscuscommons.nms.v1_21_R2;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.*;

public class NMSPackets extends NMSCommon implements me.lojosho.hibiscuscommons.nms.NMSPackets {

    static Constructor<ClientboundSetPassengersPacket> passengerConstructor;
    static Constructor<ClientboundSetEntityLinkPacket> linkConstructor;
    static Constructor<ClientboundSetCameraPacket> cameraConstructor;
    static Constructor<ClientboundPlayerLookAtPacket> lookAtConstructor;
    static {
        try {
            passengerConstructor = ClientboundSetPassengersPacket.class.getDeclaredConstructor(FriendlyByteBuf.class);
            passengerConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            linkConstructor = ClientboundSetEntityLinkPacket.class.getDeclaredConstructor(FriendlyByteBuf.class);
            linkConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            cameraConstructor = ClientboundSetCameraPacket.class.getDeclaredConstructor(FriendlyByteBuf.class);
            cameraConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            lookAtConstructor = ClientboundPlayerLookAtPacket.class.getDeclaredConstructor(FriendlyByteBuf.class);
            lookAtConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        byteBuf.writeInt(leashEntity);
        byteBuf.writeInt(entityId);
        try {
            ClientboundSetEntityLinkPacket packet = linkConstructor.newInstance(byteBuf);
            for (Player p : sendTo) sendPacket(p, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void sendRotationPacket(int entityId, float yaw, boolean onGround, List<Player> sendTo) {
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        byteBuf.writeVarInt(entityId);
        byteBuf.writeFloat(yaw);
        byteBuf.writeBoolean(onGround);
        try {
            ClientboundPlayerLookAtPacket packet = lookAtConstructor.newInstance(byteBuf);
            for (Player p : sendTo) sendPacket(p, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCameraPacket(int entityId, List<Player> sendTo) {
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        byteBuf.writeVarInt(entityId);
        try {
            ClientboundSetCameraPacket packet = cameraConstructor.newInstance(byteBuf);
            for (Player p : sendTo) sendPacket(p, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
