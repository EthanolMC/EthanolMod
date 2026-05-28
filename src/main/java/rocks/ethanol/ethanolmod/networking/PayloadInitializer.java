package rocks.ethanol.ethanolmod.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundAuthDataPayload;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundCommandTreePayload;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundMessagePayload;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundSuggestionsResponsePayload;
import rocks.ethanol.ethanolmod.networking.impl.clientbound.ClientboundVanishPayload;
import rocks.ethanol.ethanolmod.networking.impl.serverbound.ServerboundAuthInitPacket;
import rocks.ethanol.ethanolmod.networking.impl.serverbound.ServerboundAuthResponsePacket;
import rocks.ethanol.ethanolmod.networking.impl.serverbound.ServerboundCommandPayload;
import rocks.ethanol.ethanolmod.networking.impl.serverbound.ServerboundRequestSuggestionsPayload;
import rocks.ethanol.ethanolmod.networking.impl.shared.SharedInitPayload;

public class PayloadInitializer {

    public static void init() {
        PayloadTypeRegistry.serverboundPlay().register(SharedInitPayload.ID, SharedInitPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerboundCommandPayload.ID, ServerboundCommandPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerboundRequestSuggestionsPayload.ID, ServerboundRequestSuggestionsPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerboundAuthInitPacket.ID, ServerboundAuthInitPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerboundAuthResponsePacket.ID, ServerboundAuthResponsePacket.CODEC);

        PayloadTypeRegistry.clientboundPlay().register(SharedInitPayload.ID, SharedInitPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundVanishPayload.ID, ClientboundVanishPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundMessagePayload.ID, ClientboundMessagePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundCommandTreePayload.ID, ClientboundCommandTreePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundSuggestionsResponsePayload.ID, ClientboundSuggestionsResponsePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundAuthDataPayload.ID, ClientboundAuthDataPayload.CODEC);
    }

}
