package rocks.ethanol.ethanolmod.eventhandler.impl;

import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.networking.impl.both.InitEthanolBothDirectionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

public class JoinEventHandler implements ClientPlayConnectionEvents.Join {

    @Override
    public void onPlayReady(final ClientPlayNetworkHandler handler, final PacketSender sender, final MinecraftClient client) {
        if (EthanolMod.getInstance().isInstalled() && !EthanolMod.getInstance().hasSend()) {
            EthanolMod.getInstance().setSend(true);
            handler.sendPacket(new CustomPayloadC2SPacket(new InitEthanolBothDirectionPayload()));
        }
    }

}
