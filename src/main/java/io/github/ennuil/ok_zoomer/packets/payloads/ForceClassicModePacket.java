package io.github.ennuil.ok_zoomer.packets.payloads;

import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ModUtils;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public record ForceClassicModePacket() implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, ForceClassicModePacket> STREAM_CODEC = CustomPacketPayload.codec(ForceClassicModePacket::write, ForceClassicModePacket::new);
	public static final CustomPacketPayload.Type<ForceClassicModePacket> TYPE = new Type<>(ModUtils.id("force_classic_mode"));

	public ForceClassicModePacket(FriendlyByteBuf buf) {
		this();
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void write(FriendlyByteBuf buffer) {
		// NO-OP
	}

	@ClientOnly
	public static void handle(ForceClassicModePacket payload, ClientConfigurationNetworking.Context context) {
		ZoomUtils.LOGGER.info("[Ok Zoomer] This server has imposed classic mode");
		ZoomPackets.applyClassicMode();
		ZoomPackets.checkRestrictions();
	}
}
