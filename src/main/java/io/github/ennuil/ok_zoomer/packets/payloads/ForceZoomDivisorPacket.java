package io.github.ennuil.ok_zoomer.packets.payloads;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public record ForceZoomDivisorPacket(double max, double min) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, ForceZoomDivisorPacket> STREAM_CODEC = CustomPacketPayload.codec(ForceZoomDivisorPacket::write, ForceZoomDivisorPacket::new);
	public static final CustomPacketPayload.Type<ForceZoomDivisorPacket> TYPE = CustomPacketPayload.createType("ok_zoomer:force_zoom_divisor");

	public ForceZoomDivisorPacket(FriendlyByteBuf buf) {
		this(buf.readDouble(), buf.readableBytes() >= 8 ? buf.readDouble() : buf.getDouble(0));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeDouble(this.max());
		buffer.writeDouble(this.min());
	}

	@ClientOnly
	public static void handle(ForceZoomDivisorPacket payload, ClientConfigurationNetworking.Context context) {
		if ((payload.min() <= 0.0 || payload.max() <= 0.0) || payload.min() > payload.max()) {
			ZoomUtils.LOGGER.info("[Ok Zoomer] This server has attempted to set invalid divisor values! (min {}, max {})", payload.min(), payload.max());
		} else {
			ZoomUtils.LOGGER.info("[Ok Zoomer] This server has set the zoom divisors to minimum {} and maximum {}", payload.min(), payload.max());
			ZoomPackets.applyForcedZoomDivisor(payload.max(), payload.min());
			OkZoomerConfigManager.configureZoomInstance();
			ZoomPackets.checkRestrictions();
		}
	}
}
