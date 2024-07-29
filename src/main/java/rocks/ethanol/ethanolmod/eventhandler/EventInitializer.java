package rocks.ethanol.ethanolmod.eventhandler;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import rocks.ethanol.ethanolmod.eventhandler.impl.DisconnectEventHandler;
import rocks.ethanol.ethanolmod.eventhandler.impl.HudRenderEventHandler;
import rocks.ethanol.ethanolmod.eventhandler.impl.JoinEventHandler;
import rocks.ethanol.ethanolmod.eventhandler.impl.WorldTickEventHandler;

public class EventInitializer {

    public static void init() {
        ClientPlayConnectionEvents.DISCONNECT.register(new DisconnectEventHandler());
        ClientPlayConnectionEvents.JOIN.register(new JoinEventHandler());
        HudRenderCallback.EVENT.register(new HudRenderEventHandler());
        ClientTickEvents.START_WORLD_TICK.register(new WorldTickEventHandler());
    }

}
