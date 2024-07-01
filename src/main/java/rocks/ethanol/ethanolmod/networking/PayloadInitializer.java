package rocks.ethanol.ethanolmod.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import rocks.ethanol.ethanolmod.networking.impl.serverbound.ServerboundCommandPayload;
import rocks.ethanol.ethanolmod.networking.impl.serverbound.ServerboundRequestSuggestionsPayload;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundCommandTreePayload;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundMessagePayload;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundSuggestionsResponsePayload;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundVanishPayload;
import rocks.ethanol.ethanolmod.networking.impl.shared.SharedInitPayload;

public class PayloadInitializer {

    public static void init() {
        PayloadTypeRegistry.playC2S().register(SharedInitPayload.ID, SharedInitPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundCommandPayload.ID, ServerboundCommandPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundRequestSuggestionsPayload.ID, ServerboundRequestSuggestionsPayload.CODEC);

        PayloadTypeRegistry.playS2C().register(SharedInitPayload.ID, SharedInitPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundVanishPayload.ID, ClientboundVanishPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundMessagePayload.ID, ClientboundMessagePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundCommandTreePayload.ID, ClientboundCommandTreePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundSuggestionsResponsePayload.ID, ClientboundSuggestionsResponsePayload.CODEC);
    }

}
