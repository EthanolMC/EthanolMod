package rocks.ethanol.ethanolmod.eventhandler.impl;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.networking.impl.shared.SharedInitPayload;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

public class WorldTickEventHandler implements ClientTickEvents.StartWorldTick, MinecraftWrapper {

    @Override
    public final void onStartTick(final ClientWorld world) {
        if (this.mc.isIntegratedServerRunning()) {
            return;
        }
        final EthanolMod ethanolMod = EthanolMod.getInstance();
        if (ethanolMod.isInstalled() && !ethanolMod.hasSend()) {
            ethanolMod.setSend(true);
            this.mc.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new SharedInitPayload()));
        }
    }

}
