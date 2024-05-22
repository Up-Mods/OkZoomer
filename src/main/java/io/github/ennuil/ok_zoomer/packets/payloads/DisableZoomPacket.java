package io.github.ennuil.ok_zoomer.packets.payloads;

import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public record DisableZoomPacket() implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, DisableZoomPacket> STREAM_CODEC = CustomPacketPayload.codec(DisableZoomPacket::write, DisableZoomPacket::new);
	public static final CustomPacketPayload.Type<DisableZoomPacket> TYPE = CustomPacketPayload.createType("ok_zoomer:disable_zoom");

	public DisableZoomPacket(FriendlyByteBuf buf) {
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
	public static void handle(DisableZoomPacket payload, ClientConfigurationNetworking.Context context) {
		ZoomUtils.LOGGER.info("[Ok Zoomer] This server has disabled zooming");
		ZoomPackets.applyDisableZooming();
		ZoomPackets.checkRestrictions();
	}
}
