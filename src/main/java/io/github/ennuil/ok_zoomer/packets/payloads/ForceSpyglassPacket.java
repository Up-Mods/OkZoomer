package io.github.ennuil.ok_zoomer.packets.payloads;

import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public record ForceSpyglassPacket(boolean requireItem, boolean replaceZoom) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, ForceSpyglassPacket> STREAM_CODEC = CustomPacketPayload.codec(ForceSpyglassPacket::write, ForceSpyglassPacket::new);
	public static final CustomPacketPayload.Type<ForceSpyglassPacket> TYPE = CustomPacketPayload.createType("ok_zoomer:force_spyglass");

	public ForceSpyglassPacket(FriendlyByteBuf buf) {
		this(buf.readBoolean(), buf.readBoolean());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(this.requireItem());
		buffer.writeBoolean(this.replaceZoom());
	}

	@ClientOnly
	public static void handle(ForceSpyglassPacket payload, ClientConfigurationNetworking.Context context) {
		ZoomUtils.LOGGER.info("[Ok Zoomer] This server has the following spyglass restrictions: Require Item: {}, Replace Zoom: {}", payload.requireItem(), payload.replaceZoom());
		ZoomPackets.applySpyglassDependency(payload.requireItem(), payload.replaceZoom());
		ZoomPackets.checkRestrictions();
	}
}
