package rocks.ethanol.ethanolmod.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import rocks.ethanol.ethanolmod.networking.impl.client.CommandEthanolC2SPayload;
import rocks.ethanol.ethanolmod.networking.impl.client.RequestSuggestionsEthanolC2SPayload;
import rocks.ethanol.ethanolmod.networking.impl.server.CommandTreeEthanolS2CPayload;
import rocks.ethanol.ethanolmod.networking.impl.server.MessageEthanolS2CPayload;
import rocks.ethanol.ethanolmod.networking.impl.server.SuggestionsResponseEthanolS2CPayload;
import rocks.ethanol.ethanolmod.networking.impl.server.VanishEthanolS2CPayload;
import rocks.ethanol.ethanolmod.networking.impl.shared.InitEthanolSharedPayload;

public class PayloadInitializer {

    public static void onInit() {
        PayloadTypeRegistry.playC2S().register(InitEthanolSharedPayload.ID, InitEthanolSharedPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(CommandEthanolC2SPayload.ID, CommandEthanolC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestSuggestionsEthanolC2SPayload.ID, RequestSuggestionsEthanolC2SPayload.CODEC);

        PayloadTypeRegistry.playS2C().register(InitEthanolSharedPayload.ID, InitEthanolSharedPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(VanishEthanolS2CPayload.ID, VanishEthanolS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MessageEthanolS2CPayload.ID, MessageEthanolS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CommandTreeEthanolS2CPayload.ID, CommandTreeEthanolS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SuggestionsResponseEthanolS2CPayload.ID, SuggestionsResponseEthanolS2CPayload.CODEC);
    }

}
