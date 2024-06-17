package rocks.ethanol.ethanolmod.eventhandler.impl;

import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.networking.impl.client.CommandEthanolC2SPayload;
import rocks.ethanol.ethanolmod.utils.MinecraftWrapper;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

public class AllowChatEventHandler implements ClientSendMessageEvents.AllowChat, MinecraftWrapper {

    @Override
    public boolean allowSendChatMessage(final String message) {
        final String prefix = EthanolMod.getInstance().getConfigManager().getCommandPrefix();
        if (!EthanolMod.getInstance().isInstalled() || !message.startsWith(prefix)) return true;
        final String command = message.substring(prefix.length());
        if (command.isEmpty()) return false;
        this.mc.inGameHud.getChatHud().addToMessageHistory(message);
        this.mc.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new CommandEthanolC2SPayload(command)));
        return false;
    }

}
