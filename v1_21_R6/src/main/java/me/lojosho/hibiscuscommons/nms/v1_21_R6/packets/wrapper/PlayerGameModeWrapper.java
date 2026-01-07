package me.lojosho.hibiscuscommons.nms.v1_21_R6.packets.wrapper;

import me.lojosho.hibiscuscommons.packets.PacketType;
import me.lojosho.hibiscuscommons.packets.wrapper.PacketWrapper;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import org.bukkit.GameMode;

public class PlayerGameModeWrapper implements PacketWrapper {

    private final GameMode gameMode;

    public PlayerGameModeWrapper(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public PacketType getType() {
        return PacketType.PLAYER_GAMEMODE_CHANGE;
    }

    @Override
    public Object toNativePacket() {
        ClientboundGameEventPacket.Type type = ClientboundGameEventPacket.CHANGE_GAME_MODE;
        float param = gameMode.getValue();

        return new ClientboundGameEventPacket(type, param);
    }
}
