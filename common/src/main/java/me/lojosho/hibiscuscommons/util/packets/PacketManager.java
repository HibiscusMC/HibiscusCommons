package me.lojosho.hibiscuscommons.util.packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.google.common.primitives.Ints;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.lojosho.hibiscuscommons.nms.NMSHandlers;
import me.lojosho.hibiscuscommons.util.MessagesUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class PacketManager {
    private final static int POSITION_INTERPOLATION_DURATION = 2;

    public static void sendEntitySpawnPacket(
            final @NotNull Location location,
            final int entityId,
            final EntityType entityType,
            final UUID uuid,
            final @NotNull List<Player> sendTo
    ) {
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
            entityId,
            uuid,
            SpigotConversionUtil.fromBukkitEntityType(entityType),
            SpigotConversionUtil.fromBukkitLocation(location),
            0,
            0,
            new Vector3d()
        );

        for (Player p : sendTo) sendPacket(p, packet);
    }

    public static void gamemodeChangePacket(
            Player player,
            int gamemode
    ) {
        WrapperPlayServerChangeGameState packet = new WrapperPlayServerChangeGameState(
            3,
            gamemode
        );

        sendPacket(player, packet);
        MessagesUtil.sendDebugMessages("Gamemode Change sent to " + player + " to be " + gamemode);
    }

    public static void ridingMountPacket(
        int mountId,
        int[] passengerIds,
        @NotNull List<Player> sendTo
    ) {
        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(
            mountId,
            passengerIds
        );

        for (final Player p : sendTo) {
            sendPacket(p, packet);
        }
    }

    public static void ridingMountPacket(
            int mountId,
            int passengerId,
            @NotNull List<Player> sendTo
    ) {
        ridingMountPacket(mountId, new int[]{passengerId}, sendTo);
    }

    public static void sendLookPacket(
            int entityId,
            @NotNull Location location,
            @NotNull List<Player> sendTo
    ) {
        WrapperPlayServerEntityHeadLook packet = new WrapperPlayServerEntityHeadLook(
            entityId,
            (location.getYaw() * 256.0F / 360.0F)
        );

        for (Player p : sendTo) sendPacket(p, packet);
    }

    public static void sendRotationPacket(
            int entityId,
            @NotNull Location location,
            boolean onGround,
            @NotNull List<Player> sendTo
    ) {
        float ROTATION_FACTOR = 256.0F / 360.0F;
        float yaw = location.getYaw() * ROTATION_FACTOR;
        float pitch = location.getPitch() * ROTATION_FACTOR;

        WrapperPlayServerEntityRotation packet = new WrapperPlayServerEntityRotation(
            entityId,
            yaw,
            pitch,
            onGround
        );

        for (Player p : sendTo) sendPacket(p, packet);
    }

    public static void sendRotationPacket(
            int entityId,
            int yaw,
            boolean onGround,
            @NotNull List<Player> sendTo
    ) {
        float ROTATION_FACTOR = 256.0F / 360.0F;
        float yaw2 = yaw * ROTATION_FACTOR;

        WrapperPlayServerEntityRotation packet = new WrapperPlayServerEntityRotation(
            entityId,
            yaw2,
            0,
            onGround
        );

        for (Player p : sendTo) sendPacket(p, packet);
    }

    public static void sendRidingPacket(
            final int mountId,
            final int passengerId,
            final @NotNull List<Player> sendTo
    ) {
        sendRidingPacket(mountId, new int[] {passengerId}, sendTo);
    }

    public static void sendRidingPacket(
            final int mountId,
            final int[] passengerIds,
            final @NotNull List<Player> sendTo
    ) {
        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(
            mountId,
            passengerIds
        );

        for (final Player p : sendTo) {
            sendPacket(p, packet);
        }
    }

    /**
     * Destroys an entity from a player
     * @param entityId The entity to delete for a player
     * @param sendTo The players the packet should be sent to
     */
    public static void sendEntityDestroyPacket(final int entityId, @NotNull List<Player> sendTo) {
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(
            entityId
        );

        for (final Player p : sendTo) sendPacket(p, packet);
    }

    /**
     * Destroys an entity from a player
     * @param sendTo The players the packet should be sent to
     */
    public static void sendEntityDestroyPacket(final List<Integer> ids, @NotNull List<Player> sendTo) {
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(
            Ints.toArray(ids)
        );

        for (final Player p : sendTo) sendPacket(p, packet);
    }

    /**
     * Sends a camera packet
     * @param entityId The Entity ID that camera will go towards
     * @param sendTo The players that will be sent this packet
     */
    public static void sendCameraPacket(final int entityId, @NotNull List<Player> sendTo) {
        WrapperPlayServerCamera packet = new WrapperPlayServerCamera(entityId);

        for (final Player p : sendTo) sendPacket(p, packet);
        MessagesUtil.sendDebugMessages(sendTo + " | " + entityId + " has had a camera packet on them!");
    }

    public static void sendLeashPacket(
            final int leashedEntity,
            final int entityId,
            final @NotNull List<Player> sendTo
    ) {
        WrapperPlayServerAttachEntity packet = new WrapperPlayServerAttachEntity(
            leashedEntity,
             entityId,
            true
        );

        for (final Player p : sendTo) {
            sendPacket(p, packet);
        }
    }

    /**
     * Used when a player is sent 8+ blocks.
     * @param entityId Entity this affects
     * @param location Location a player is being teleported to
     * @param onGround If the packet is on the ground
     * @param sendTo Whom to send the packet to
     */
    public static void sendTeleportPacket(
            final int entityId,
            final @NotNull Location location,
            boolean onGround,
            final @NotNull List<Player> sendTo
    ) {
        WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(
            entityId,
            SpigotConversionUtil.fromBukkitLocation(location),
            onGround
        );

        for (final Player p : sendTo) {
            sendPacket(p, packet);
        }
    }

    @NotNull
    public static List<Player> getViewers(Location location, int distance) {
        ArrayList<Player> viewers = new ArrayList<>();
        if (distance <= 0) {
            viewers.addAll(location.getWorld().getPlayers());
        } else {
            viewers.addAll(getNearbyPlayers(location, distance));
        }
        return viewers;
    }

    public static void slotUpdate(
            Player player,
            int slot
    ) {
        WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(
            NMSHandlers.getHandler().getUtilHandler().getInventoryId(player),
            NMSHandlers.getHandler().getUtilHandler().incrementInventoryStateId(player),
            36, // TODO: Verify accuracy
            SpigotConversionUtil.fromBukkitItemStack(player.getInventory().getItem(slot))
        );

        sendPacket(player, packet);
    }

    public static void equipmentSlotUpdate(
            int entityId,
            org.bukkit.inventory.EquipmentSlot slot,
            ItemStack item,
            List<Player> sendTo
    ) {
        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(
            entityId,
            Collections.singletonList(new Equipment(
                PEConverter.convertEquipmentSlot(slot),
                SpigotConversionUtil.fromBukkitItemStack(item)
            ))
        );

        for (final Player p : sendTo) {
            sendPacket(p, packet);
        }
    }

    public static void equipmentSlotUpdate(
            int entityId,
            HashMap<org.bukkit.inventory.EquipmentSlot, ItemStack> equipmentMap,
            List<Player> sendTo
    ) {
        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(
            entityId,
            equipmentMap.entrySet().stream()
                .map(entry -> new Equipment(
                    PEConverter.convertEquipmentSlot(entry.getKey()),
                    SpigotConversionUtil.fromBukkitItemStack(entry.getValue())
                ))
                .toList()
        );

        for (final Player p : sendTo) {
            sendPacket(p, packet);
        }
    }

    // TODO: Test
    public static void sendScoreboardHideNamePacket(Player player, String name) {
        WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
            Component.empty(),
            null,
            null,
            WrapperPlayServerTeams.NameTagVisibility.NEVER,
            WrapperPlayServerTeams.CollisionRule.ALWAYS,
            NamedTextColor.WHITE,
            WrapperPlayServerTeams.OptionData.ALL
        );

        //Remove the Team (i assume so if it exists)
        WrapperPlayServerTeams removeTeamPacket = new WrapperPlayServerTeams(
            name,
            WrapperPlayServerTeams.TeamMode.REMOVE,
            (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
            Collections.emptyList()
        );
        sendPacket(player, removeTeamPacket);

        //Creating the Team
        WrapperPlayServerTeams createTeamPacket = new WrapperPlayServerTeams(
            name,
            WrapperPlayServerTeams.TeamMode.CREATE,
            teamInfo,
            Collections.emptyList()
        );
        sendPacket(player, createTeamPacket);

        //Adding players to the team (You have to use the NPC's name, and add it to a list)
        WrapperPlayServerTeams addTeamPlayersPacket = new WrapperPlayServerTeams(
            name,
            WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
            teamInfo,
            Collections.singletonList(name)
        );
        sendPacket(player, addTeamPlayersPacket);
    }

    public static void sendItemDisplayMetadata(int entityId,
                                        Vector3f translation,
                                        Vector3f scale,
                                        Quaternionf rotationLeft,
                                        Quaternionf rotationRight,
                                        Display.Billboard billboard,
                                        int blockLight, int skyLight, float viewRange, float width, float height,
                                        ItemDisplay.ItemDisplayTransform transform, ItemStack itemStack,
                                        List<Player> sendTo) {
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(
            entityId,
            List.of(
                new EntityData(10, EntityDataTypes.INT, POSITION_INTERPOLATION_DURATION),
                new EntityData(11, EntityDataTypes.VECTOR3F, translation),
                new EntityData(12, EntityDataTypes.VECTOR3F, scale),
                new EntityData(13, EntityDataTypes.QUATERNION, rotationLeft),
                new EntityData(14, EntityDataTypes.QUATERNION, rotationRight),
                new EntityData(15, EntityDataTypes.BYTE, (byte) billboard.ordinal()),
                new EntityData(16, EntityDataTypes.INT, (blockLight << 4 | skyLight << 20)),
                new EntityData(17, EntityDataTypes.FLOAT, viewRange),
                new EntityData(20, EntityDataTypes.FLOAT, width),
                new EntityData(21, EntityDataTypes.FLOAT, height),
                new EntityData(23, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(itemStack)),
                new EntityData(24, EntityDataTypes.BYTE, (byte) transform.ordinal())
            )
        );

        for (final Player p : sendTo) {
            sendPacket(p, packet);
        }
    }

    private static List<Player> getNearbyPlayers(Location location, int distance) {
        List<Player> players = new ArrayList<>();
        for (Entity entity : location.getWorld().getNearbyEntities(location, distance, distance, distance)) {
            if (entity instanceof Player) {
                players.add((Player) entity);
            }
        }
        return players;
    }

    public static void sendPacket(Player player, PacketWrapper<?> packet) {
        if (player == null) return;
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
}
