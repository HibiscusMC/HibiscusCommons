package me.lojosho.hibiscuscommons.nms.v1_21_R1.packets.wrapper;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.papermc.paper.adventure.PaperAdventure;
import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import me.lojosho.hibiscuscommons.util.AdventureUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.UUID;

public class PlayerInfoAddWrapper implements PacketWrapper {

    private final Player skinnedPlayer;
    private final int entityId;
    private final UUID uuid;
    private final String npcName;


    public PlayerInfoAddWrapper(final Player skinnedPlayer,
                                final int entityId,
                                final UUID uuid,
                                final String npcName) {
        this.skinnedPlayer = skinnedPlayer;
        this.entityId = entityId;
        this.uuid = uuid;
        this.npcName = npcName;
    }

    @Override
    public PacketType getType() {
        return PacketType.PLAYER_INFO_ADD;
    }

    @Override
    public Object toNativePacket() {
        ServerPlayer player = ((CraftPlayer) skinnedPlayer).getHandle();
        String name = npcName;
        if (name.length() > 15) name = name.substring(0, 15);
        Property property = ((CraftPlayer) skinnedPlayer).getProfile().getProperties().get("textures").stream().findAny().orElse(null);

        GameProfile profile = new GameProfile(uuid, name);
        if (property != null) profile.getProperties().put("textures", property);

        Component component = AdventureUtils.MINI_MESSAGE.deserialize(name);
        net.minecraft.network.chat.Component nmsComponent = HibiscusCommonsPlugin.isOnPaper() ? PaperAdventure.asVanilla(component) : net.minecraft.network.chat.Component.literal(name);

        RemoteChatSession.Data chatData = null;
        RemoteChatSession session = player.getChatSession();
        if (session != null) chatData = player.getChatSession().asData();

        ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(uuid, profile, false, 0, GameType.CREATIVE, nmsComponent, chatData);
        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        return new ClientboundPlayerInfoUpdatePacket(actions, entry);
    }
}
