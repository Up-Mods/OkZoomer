package io.github.ennuil.ok_zoomer.packets.payloads;

import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public record ForceSpyglassOverlayPacket() implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, ForceSpyglassOverlayPacket> STREAM_CODEC = CustomPacketPayload.codec(ForceSpyglassOverlayPacket::write, ForceSpyglassOverlayPacket::new);
	public static final CustomPacketPayload.Type<ForceSpyglassOverlayPacket> TYPE = CustomPacketPayload.createType("ok_zoomer:force_spyglass_overlay");

	public ForceSpyglassOverlayPacket(FriendlyByteBuf buf) {
		this();
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void write(FriendlyByteBuf buffer) {
		// NO-OP
	}

	@ClientOnly
	public static void handle(ForceSpyglassOverlayPacket payload, ClientConfigurationNetworking.Context context) {
		ZoomUtils.LOGGER.info("[Ok Zoomer] This server has imposed a spyglass overlay on the zoom");
		ZoomPackets.applyForceSpyglassOverlay();
		ZoomPackets.checkRestrictions();
	}
}
