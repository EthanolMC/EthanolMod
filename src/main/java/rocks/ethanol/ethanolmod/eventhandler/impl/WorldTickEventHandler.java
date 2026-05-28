package rocks.ethanol.ethanolmod.eventhandler.impl;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import rocks.ethanol.ethanolmod.EthanolMod;
import rocks.ethanol.ethanolmod.networking.impl.shared.SharedInitPayload;
import rocks.ethanol.ethanolmod.structure.MinecraftWrapper;

public class WorldTickEventHandler implements ClientTickEvents.StartLevelTick, MinecraftWrapper {

    @Override
    public final void onStartTick(final ClientLevel world) {
        if (mc.hasSingleplayerServer()) {
            return;
        }

        final EthanolMod ethanolMod = EthanolMod.getInstance();
        if (ethanolMod.isInstalled() && !ethanolMod.hasSend() && mc.getConnection() != null) {
            ethanolMod.setSend(true);
            mc.getConnection().send(new ServerboundCustomPayloadPacket(new SharedInitPayload()));
        }
    }

}
