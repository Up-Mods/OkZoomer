package io.github.ennuil.ok_zoomer.packets.payloads;

import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public record DisableZoomScrollingPacket() implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, DisableZoomScrollingPacket> STREAM_CODEC = CustomPacketPayload.codec(DisableZoomScrollingPacket::write, DisableZoomScrollingPacket::new);
	public static final CustomPacketPayload.Type<DisableZoomScrollingPacket> TYPE = CustomPacketPayload.createType("ok_zoomer:disable_zoom_scrolling");

	public DisableZoomScrollingPacket(FriendlyByteBuf buf) {
		this();
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void write(FriendlyByteBuf buffer) {
		throw new UnsupportedOperationException("Packet is read-only");
	}

	@ClientOnly
	public static void handle(DisableZoomScrollingPacket payload, ClientConfigurationNetworking.Context context) {
		ZoomUtils.LOGGER.info("[Ok Zoomer] This server has disabled zoom scrolling");
		ZoomPackets.applyDisableZoomScrolling();
		ZoomPackets.checkRestrictions();
	}
}
