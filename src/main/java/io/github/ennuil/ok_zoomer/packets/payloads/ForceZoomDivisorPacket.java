package io.github.ennuil.ok_zoomer.packets.payloads;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketSender;

public record ForceZoomDivisorPacket(double max, double min) implements CustomPacketPayload {

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeDouble(this.max());
		buffer.writeDouble(this.min());
	}

	@Override
	public ResourceLocation id() {
		return ZoomPackets.FORCE_ZOOM_DIVISOR_PACKET_ID;
	}

	public static ForceZoomDivisorPacket fromPacket(FriendlyByteBuf buf) {
		var max = buf.readDouble();
		var min = max;
		if (buf.readableBytes() >= 8) {
			min = buf.readDouble();
		}
		return new ForceZoomDivisorPacket(max, min);
	}

	@ClientOnly
	public static void handle(Minecraft client, ClientConfigurationPacketListenerImpl packetListener, ForceZoomDivisorPacket payload, PacketSender<CustomPacketPayload> sender) {
		client.execute(() -> {
			if ((payload.min() <= 0.0 || payload.max() <= 0.0) || payload.min() > payload.max()) {
				ZoomUtils.LOGGER.info("[Ok Zoomer] This server has attempted to set invalid divisor values! (min {}, max {})", payload.min(), payload.max());
			} else {
				ZoomUtils.LOGGER.info("[Ok Zoomer] This server has set the zoom divisors to minimum {} and maximum {}", payload.min(), payload.max());
				ZoomPackets.applyForcedZoomDivisor(payload.max(), payload.min());
				OkZoomerConfigManager.configureZoomInstance();
				ZoomPackets.checkRestrictions();
			}
		});
	}
}
