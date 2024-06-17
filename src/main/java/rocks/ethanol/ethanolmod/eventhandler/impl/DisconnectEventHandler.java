package rocks.ethanol.ethanolmod.eventhandler.impl;

import rocks.ethanol.ethanolmod.EthanolMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class DisconnectEventHandler implements ClientPlayConnectionEvents.Disconnect {

    @Override
    public void onPlayDisconnect(final ClientPlayNetworkHandler handler, final MinecraftClient client) {
        EthanolMod.getInstance().setInstalled(false);
        EthanolMod.getInstance().setSend(false);
        EthanolMod.getInstance().setVanished(false);
    }

}
