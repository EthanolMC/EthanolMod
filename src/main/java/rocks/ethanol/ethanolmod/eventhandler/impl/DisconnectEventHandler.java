package rocks.ethanol.ethanolmod.eventhandler.impl;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import rocks.ethanol.ethanolmod.EthanolMod;

public class DisconnectEventHandler implements ClientPlayConnectionEvents.Disconnect {

    @Override
    public final void onPlayDisconnect(final ClientPlayNetworkHandler handler, final MinecraftClient client) {
        final EthanolMod ethanolMod = EthanolMod.getInstance();
        ethanolMod.setInstalled(false);
        ethanolMod.setSend(false);
        ethanolMod.setVanished(false);
        ethanolMod.resetCommandDispatcher();
        ethanolMod.getPendingRequests().clear();
    }

}
