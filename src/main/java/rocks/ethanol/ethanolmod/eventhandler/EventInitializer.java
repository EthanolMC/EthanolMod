package rocks.ethanol.ethanolmod.eventhandler;

import rocks.ethanol.ethanolmod.eventhandler.impl.AllowChatEventHandler;
import rocks.ethanol.ethanolmod.eventhandler.impl.DisconnectEventHandler;
import rocks.ethanol.ethanolmod.eventhandler.impl.HudRenderEventHandler;
import rocks.ethanol.ethanolmod.eventhandler.impl.JoinEventHandler;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class EventInitializer {

    public static void onInit() {
        ClientPlayConnectionEvents.DISCONNECT.register(new DisconnectEventHandler());
        HudRenderCallback.EVENT.register(new HudRenderEventHandler());
        ClientSendMessageEvents.ALLOW_CHAT.register(new AllowChatEventHandler());
        ClientPlayConnectionEvents.JOIN.register(new JoinEventHandler());
    }

}
