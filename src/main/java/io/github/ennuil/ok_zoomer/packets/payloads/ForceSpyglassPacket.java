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

public record ForceSpyglassPacket(boolean requireItem, boolean replaceZoom) implements CustomPacketPayload {

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(this.requireItem());
		buffer.writeBoolean(this.replaceZoom());
	}

	@Override
	public ResourceLocation id() {
		return ZoomPackets.FORCE_SPYGLASS_PACKET_ID;
	}

	public static ForceSpyglassPacket fromPacket(FriendlyByteBuf buf) {
		var requireItem = buf.readBoolean();
		var replaceZoom = buf.readBoolean();
		return new ForceSpyglassPacket(requireItem, replaceZoom);
	}


	@ClientOnly
	public static void handle(Minecraft client, ClientConfigurationPacketListenerImpl packetListener, ForceSpyglassPacket payload, PacketSender<CustomPacketPayload> sender) {
		client.execute(() -> {
			ZoomUtils.LOGGER.info("[Ok Zoomer] This server has the following spyglass restrictions: Require Item: {}, Replace Zoom: {}", payload.requireItem(), payload.replaceZoom());
			ZoomPackets.applySpyglassDependency(payload.requireItem(), payload.replaceZoom());
			ZoomPackets.checkRestrictions();
		});
	}
}
