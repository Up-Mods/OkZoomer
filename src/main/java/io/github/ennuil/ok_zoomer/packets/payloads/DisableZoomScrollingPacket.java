package io.github.ennuil.ok_zoomer.packets.payloads;

import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketSender;

public record DisableZoomScrollingPacket() implements CustomPacketPayload {

	@Override
	public void write(FriendlyByteBuf buffer) {
		throw new UnsupportedOperationException("Packet is read-only");
	}

	@Override
	public ResourceLocation id() {
		return ZoomPackets.DISABLE_ZOOM_SCROLLING_PACKET_ID;
	}

	public static DisableZoomScrollingPacket fromPacket(FriendlyByteBuf buf) {
		return new DisableZoomScrollingPacket();
	}

	@ClientOnly
	public static void handle(Minecraft client, ClientConfigurationPacketListenerImpl packetListener, DisableZoomScrollingPacket payload, PacketSender<CustomPacketPayload> sender) {
		client.execute(() -> {
			ZoomUtils.LOGGER.info("[Ok Zoomer] This server has disabled zoom scrolling");
			ZoomPackets.applyDisableZoomScrolling();
			ZoomPackets.checkRestrictions();
		});
	}
}
