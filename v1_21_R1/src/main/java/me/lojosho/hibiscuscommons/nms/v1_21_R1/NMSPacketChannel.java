package me.lojosho.hibiscuscommons.nms.v1_21_R1;

import com.mojang.datafixers.util.Pair;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import me.lojosho.hibiscuscommons.nms.NMSHandlers;
import me.lojosho.hibiscuscommons.packets.PacketAction;
import me.lojosho.hibiscuscommons.packets.wrapper.*;
import me.lojosho.hibiscuscommons.plugins.SubPlugins;
import me.lojosho.hibiscuscommons.util.MessagesUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class NMSPacketChannel extends ChannelDuplexHandler {

    @Getter
    private final Player player;

    public NMSPacketChannel(Player player) {
        this.player = player;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof Packet packet)) {
            super.write(ctx, msg, promise);
            return;
        }

        switch (packet) {
            case ClientboundContainerSetContentPacket setContentPacket -> msg = handleMenuChange(setContentPacket);
            case ClientboundContainerSetSlotPacket setSlotPacket -> msg = handleSlotChange(setSlotPacket);
            case ClientboundSetEquipmentPacket equipmentPacket -> msg = handlePlayerEquipment(equipmentPacket);
            case ClientboundSetPassengersPacket passengerPacket -> msg = handlePassengerSet(passengerPacket);
            case ClientboundUpdateAttributesPacket attributesPacket -> msg = handleScaleChange(attributesPacket);
            default -> {}
        }

        if (msg == null) return;
        else super.write(ctx, msg, promise);
    }

    private Packet<?> handleMenuChange(@NotNull ClientboundContainerSetContentPacket packet) {
        MessagesUtil.sendDebugMessages("ClientboundContainerSetContentPacket");
        MessagesUtil.sendDebugMessages("Menu Initial ");

        Integer windowId = packet.getContainerId();
        List<ItemStack> slotData = packet.getItems();

        List<org.bukkit.inventory.ItemStack> bukkitItems = new ArrayList<>();
        for (ItemStack nmsItem : slotData) {
            if (nmsItem == null) {
                slotData.add(null);
                continue;
            }
            bukkitItems.add(CraftItemStack.asBukkitCopy(nmsItem));
        }

        AtomicReference<PacketAction> action = new AtomicReference<>(PacketAction.NOTHING);
        ContainerContentWrapper wrapper = new ContainerContentWrapper(windowId, bukkitItems);
        SubPlugins.getSubPlugins().forEach(plugin -> {

            PacketAction pluginAction = plugin.getPacketInterface().writeContainerContent(player, wrapper);
            if (pluginAction != PacketAction.NOTHING) action.set(pluginAction);
        });

        if (action.get() == PacketAction.CANCELLED) return null;
        if (action.get() == PacketAction.NOTHING) return packet;

        NonNullList<ItemStack> nmsItems = NonNullList.create();
        for (org.bukkit.inventory.ItemStack bukkitItem : bukkitItems) {
            if (bukkitItem == null) {
                slotData.add(null);
                continue;
            }
            nmsItems.add(CraftItemStack.asNMSCopy(bukkitItem));
        }

        return new ClientboundContainerSetContentPacket(wrapper.getWindowId(), packet.getStateId(), nmsItems, packet.getCarriedItem());
    }

    private Packet<?> handleSlotChange(@NotNull ClientboundContainerSetSlotPacket packet) {
        MessagesUtil.sendDebugMessages("ClientboundContainerSetSlotPacket");

        final int windowId = packet.getContainerId();
        final int slot = packet.getSlot();
        final ItemStack item = packet.getItem();

        org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.asBukkitCopy(item);

        AtomicReference<PacketAction> action = new AtomicReference<>(PacketAction.NOTHING);
        SlotContentWrapper wrapper = new SlotContentWrapper(windowId, slot, bukkitItem);

        SubPlugins.getSubPlugins().forEach(plugin -> {
            PacketAction pluginAction = plugin.getPacketInterface().writeSlotContent(player, wrapper);
            if (pluginAction != PacketAction.NOTHING) action.set(pluginAction);
        });

        if (action.get() == PacketAction.CANCELLED) return null;
        if (action.get() == PacketAction.NOTHING) return packet;

        final ItemStack nmsItem = CraftItemStack.asNMSCopy(wrapper.getItemStack());

        return new ClientboundContainerSetSlotPacket(packet.getContainerId(), packet.getStateId(), wrapper.getSlot(), nmsItem);
    }

    private Packet<?> handlePlayerEquipment(@NotNull ClientboundSetEquipmentPacket packet) {
        MessagesUtil.sendDebugMessages("ClientboundSetEquipmentPacket");
        final List<Pair<net.minecraft.world.entity.EquipmentSlot, ItemStack>> nmsArmor = packet.getSlots();
        final int entity = packet.getEntity();
        HashMap<EquipmentSlot, org.bukkit.inventory.ItemStack> bukkitArmor = new HashMap<>();
        for (Pair<net.minecraft.world.entity.EquipmentSlot, ItemStack> piece : nmsArmor) {
            EquipmentSlot slot = CraftEquipmentSlot.getSlot(piece.getFirst());
            org.bukkit.inventory.ItemStack itemStack = CraftItemStack.asBukkitCopy(piece.getSecond());
            bukkitArmor.put(slot, itemStack);
        }

        AtomicReference<PacketAction> action = new AtomicReference<>(PacketAction.NOTHING);
        EntityEquipmentWrapper wrapper = new EntityEquipmentWrapper(entity, bukkitArmor);

        SubPlugins.getSubPlugins().forEach(plugin -> {
            PacketAction pluginAction = plugin.getPacketInterface().writeEquipmentContent(player, wrapper);
            if (pluginAction != PacketAction.NOTHING) action.set(pluginAction);
        });

        if (action.get() == PacketAction.CANCELLED) return null;
        if (action.get() == PacketAction.NOTHING) return packet;

        List<Pair<net.minecraft.world.entity.EquipmentSlot, ItemStack>> newArmor = new ArrayList<>();
        for (Map.Entry<EquipmentSlot, org.bukkit.inventory.ItemStack> entry : wrapper.getArmor().entrySet()) {
            net.minecraft.world.entity.EquipmentSlot slot = CraftEquipmentSlot.getNMS(entry.getKey());
            ItemStack itemStack = CraftItemStack.asNMSCopy(entry.getValue());
            newArmor.add(new Pair<>(slot, itemStack));
        }

        return new ClientboundSetEquipmentPacket(packet.getEntity(), newArmor);
    }

    private Packet<?> handlePassengerSet(@NotNull ClientboundSetPassengersPacket packet) {
        MessagesUtil.sendDebugMessages("ClientboundSetPassengersPacket");
        int ownerId = packet.getVehicle();
        List<Integer> passengers = Arrays.stream(packet.getPassengers()).boxed().collect(Collectors.toList());
        MessagesUtil.sendDebugMessages("Mount Packet Sent - Read - EntityID: " + ownerId);

        AtomicReference<PacketAction> action = new AtomicReference<>(PacketAction.NOTHING);
        PassengerWrapper wrapper = new PassengerWrapper(ownerId, passengers);
        SubPlugins.getSubPlugins().forEach(plugin -> {
            PacketAction pluginAction = plugin.getPacketInterface().writePassengerContent(player, wrapper);
            if (pluginAction != PacketAction.NOTHING) {
                action.set(pluginAction);
            }
        });

        if (action.get() == PacketAction.CANCELLED) return null;
        if (action.get() == PacketAction.NOTHING) return packet;
        return (Packet<?>) NMSHandlers.getHandler().getPacketHandler().createMountPacket(ownerId, passengers.stream().mapToInt(Integer::intValue).toArray());
    }

    private Packet<?> handleScaleChange(@NotNull ClientboundUpdateAttributesPacket packet) {
        final List<ClientboundUpdateAttributesPacket.AttributeSnapshot> nmsAttributes = packet.getValues();
        final ClientboundUpdateAttributesPacket.AttributeSnapshot nmsScaleAttribute = nmsAttributes.stream()
            .filter(attribute -> attribute.attribute().equals(Attributes.SCALE))
            .findFirst()
            .orElse(null);

        if (nmsScaleAttribute == null) {
            return packet;
        }

        AtomicReference<PacketAction> action = new AtomicReference<>(PacketAction.NOTHING);

        final double base = nmsScaleAttribute.base();
        double total = base;

        for (AttributeModifier modifier : nmsScaleAttribute.modifiers()) {
            switch (modifier.operation()) {
                case ADD_VALUE -> total += modifier.amount();
                case ADD_MULTIPLIED_BASE -> total += modifier.amount() * nmsScaleAttribute.base();
                case ADD_MULTIPLIED_TOTAL -> total += modifier.amount() * total;
            }
        }
        PlayerScaleWrapper wrapper = new PlayerScaleWrapper(packet.getEntityId(), base, total);

        SubPlugins.getSubPlugins().forEach(plugin -> {
            PacketAction pluginAction = plugin.getPacketInterface().readPlayerScale(player, wrapper);
            if (pluginAction != PacketAction.NOTHING) action.set(pluginAction);
        });

        if (action.get() == PacketAction.CANCELLED) return null;
        if (action.get() == PacketAction.NOTHING) return packet;
        return packet;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Packet packet)) {
            super.channelRead(ctx, msg);
            return;
        }

        switch (packet) {
            case ServerboundContainerClickPacket clickPacket -> msg = handleInventoryClick(clickPacket);
            case ServerboundPlayerActionPacket playerActionPacket -> msg = handlePlayerAction(playerActionPacket);
            case ServerboundSwingPacket swingPacket -> msg = handlePlayerArm(swingPacket);
            case ServerboundInteractPacket interactPacket -> msg = handleInteract(interactPacket);
            default -> {}
        }

        if (msg == null) return;
        else super.channelRead(ctx, msg);
    }

    private Packet<?> handleInventoryClick(@NotNull ServerboundContainerClickPacket packet) {
        MessagesUtil.sendDebugMessages("ServerboundContainerClickPacket");
        ClickType clickType = packet.getClickType();
        int slotClicked = packet.getSlotNum();

        AtomicReference<PacketAction> action = new AtomicReference<>(PacketAction.NOTHING);
        SubPlugins.getSubPlugins().forEach(plugin -> {

            PacketAction pluginAction = plugin.getPacketInterface().readInventoryClick(player, new InventoryClickWrapper(clickType.ordinal(), slotClicked));
            if (pluginAction != PacketAction.NOTHING) action.set(pluginAction);

        });
        if (action.get() == PacketAction.CANCELLED) return null;
        return packet;
    }

    private Packet<?> handlePlayerAction(ServerboundPlayerActionPacket packet) {
        MessagesUtil.sendDebugMessages("ServerboundPlayerActionPacket");
        ServerboundPlayerActionPacket.Action playerAction = packet.getAction();

        AtomicReference<PacketAction> action = new AtomicReference<>(PacketAction.NOTHING);
        SubPlugins.getSubPlugins().forEach(plugin -> {

            PacketAction pluginAction = plugin.getPacketInterface().readPlayerAction(player, new PlayerActionWrapper(playerAction.name()));
            if (pluginAction != PacketAction.NOTHING) action.set(pluginAction);

        });
        if (action.get() == PacketAction.CANCELLED) return null;
        return packet;
    }

    private Packet<?> handlePlayerArm(@NotNull ServerboundSwingPacket packet) {
        MessagesUtil.sendDebugMessages("ServerboundSwingPacket");
        PlayerSwingWrapper wrapper = new PlayerSwingWrapper(packet.getHand().name());

        AtomicReference<PacketAction> action = new AtomicReference<>(PacketAction.NOTHING);
        SubPlugins.getSubPlugins().forEach(plugin -> {

            PacketAction pluginAction = plugin.getPacketInterface().readPlayerArm(player, wrapper);
            if (pluginAction != PacketAction.NOTHING) action.set(pluginAction);

        });
        if (action.get() == PacketAction.CANCELLED) return null;
        return packet;
    }

    private Packet<?> handleInteract(@NotNull ServerboundInteractPacket packet) {
        MessagesUtil.sendDebugMessages("ServerboundInteractPacket");

        PlayerInteractWrapper wrapper = new PlayerInteractWrapper(packet.getEntityId());

        AtomicReference<PacketAction> action = new AtomicReference<>(PacketAction.NOTHING);
        SubPlugins.getSubPlugins().forEach(plugin -> {

            PacketAction pluginAction = plugin.getPacketInterface().readEntityHandle(player, wrapper);
            if (pluginAction != PacketAction.NOTHING) action.set(pluginAction);

        });
        if (action.get() == PacketAction.CANCELLED) return null;
        return packet;
    }
}