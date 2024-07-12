package rocks.ethanol.ethanolmod.eventhandler.impl;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.auth.AuthOptions;
import rocks.ethanol.ethanolmod.auth.key.AuthKeyPair;
import rocks.ethanol.ethanolmod.networking.impl.serverbound.ServerboundAuthInitPacket;

public class JoinEventHandler implements ClientPlayConnectionEvents.Join {

    @Override
    public final void onPlayReady(final ClientPlayNetworkHandler handler, final PacketSender sender, final MinecraftClient client) {
        final AuthOptions options = EthanolMod.getInstance().getAuthOptions();
        switch (options.getMode()) {
            case SEMI_AUTOMATIC -> {
                if (!EthanolMod.getInstance().getAuthOptions().getKnownHosts().contains(handler.getServerInfo().address)) {
                    return;
                }
            }

            case AUTOMATIC -> { }

            default -> {
                return;
            }
        }

        handler.sendPacket(new CustomPayloadC2SPacket(new ServerboundAuthInitPacket(EthanolMod.getInstance().getAuthKeyPairs().getKeyPairs().stream().map(AuthKeyPair::hash).toArray(byte[][]::new))));
    }
}
