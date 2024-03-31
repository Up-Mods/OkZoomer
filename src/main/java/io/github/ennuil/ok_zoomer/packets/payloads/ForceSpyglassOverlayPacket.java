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

public record ForceSpyglassOverlayPacket() implements CustomPacketPayload {

	@Override
	public void write(FriendlyByteBuf buffer) {
		// NO-OP
	}

	@Override
	public ResourceLocation id() {
		return ZoomPackets.FORCE_SPYGLASS_OVERLAY_PACKET_ID;
	}

	public static ForceSpyglassOverlayPacket fromPacket(FriendlyByteBuf buf) {
		return new ForceSpyglassOverlayPacket();
	}

	@ClientOnly
	public static void handle(Minecraft client, ClientConfigurationPacketListenerImpl packetListener, ForceSpyglassOverlayPacket payload, PacketSender<CustomPacketPayload> sender) {
		client.execute(() -> {
			ZoomUtils.LOGGER.info("[Ok Zoomer] This server has imposed a spyglass overlay on the zoom");
			ZoomPackets.applyForceSpyglassOverlay();
			ZoomPackets.checkRestrictions();
		});
	}
}
