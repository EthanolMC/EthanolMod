package rocks.ethanol.ethanolmod.eventhandler.impl;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.auth.AuthOptions;
import rocks.ethanol.ethanolmod.auth.key.AuthKeyPair;
import rocks.ethanol.ethanolmod.networking.impl.serverbound.ServerboundAuthInitPacket;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

public class JoinEventHandler implements ClientPlayConnectionEvents.Join, MinecraftWrapper {

    @Override
    public final void onPlayReady(final ClientPacketListener handler, final PacketSender sender, final Minecraft client) {
        EthanolMod.getInstance().resetModState();

        if (mc.hasSingleplayerServer()) {
            return;
        }

        final AuthOptions options = EthanolMod.getInstance().getAuthOptions();
        final ServerData serverData = handler.getServerData();
        switch (options.getMode()) {
            case SEMI_AUTOMATIC -> {
                if (serverData == null || !options.getKnownHosts().contains(serverData.ip)) {
                    return;
                }
            }

            case AUTOMATIC -> { }

            default -> {
                return;
            }
        }

        handler.send(new ServerboundCustomPayloadPacket(new ServerboundAuthInitPacket(EthanolMod.getInstance().getAuthKeyPairs().getKeyPairs().stream().map(AuthKeyPair::hash).toArray(byte[][]::new))));
    }
}
