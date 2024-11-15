package me.lojosho.hibiscuscommons.nms.v1_21_R2;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class NMSPackets extends NMSCommon implements me.lojosho.hibiscuscommons.nms.NMSPackets {

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
    }}
