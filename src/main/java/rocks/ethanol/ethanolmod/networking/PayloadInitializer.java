package rocks.ethanol.ethanolmod.networking;

import rocks.ethanol.ethanolmod.networking.impl.both.InitEthanolBothDirectionPayload;
import rocks.ethanol.ethanolmod.networking.impl.client.CommandEthanolC2SPayload;
import rocks.ethanol.ethanolmod.networking.impl.server.MessageEthanolS2CPayload;
import rocks.ethanol.ethanolmod.networking.impl.server.VanishEthanolS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class PayloadInitializer {

    public static void onInit() {
        PayloadTypeRegistry.playC2S().register(InitEthanolBothDirectionPayload.ID, InitEthanolBothDirectionPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(CommandEthanolC2SPayload.ID, CommandEthanolC2SPayload.CODEC);

        PayloadTypeRegistry.playS2C().register(InitEthanolBothDirectionPayload.ID, InitEthanolBothDirectionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(VanishEthanolS2CPayload.ID, VanishEthanolS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MessageEthanolS2CPayload.ID, MessageEthanolS2CPayload.CODEC);
    }

}
