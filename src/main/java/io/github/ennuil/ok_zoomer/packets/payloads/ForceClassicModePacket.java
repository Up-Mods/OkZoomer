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

public record ForceClassicModePacket() implements CustomPacketPayload {

	@Override
	public void write(FriendlyByteBuf buffer) {
		// NO-OP
	}

	@Override
	public ResourceLocation id() {
		return ZoomPackets.FORCE_CLASSIC_MODE_PACKET_ID;
	}

	public static ForceClassicModePacket fromPacket(FriendlyByteBuf buf) {
		return new ForceClassicModePacket();
	}

	@ClientOnly
	public static void handle(Minecraft client, ClientConfigurationPacketListenerImpl packetListener, ForceClassicModePacket payload, PacketSender<CustomPacketPayload> sender) {
		client.execute(() -> {
			ZoomUtils.LOGGER.info("[Ok Zoomer] This server has imposed classic mode");
			ZoomPackets.applyClassicMode();
			ZoomPackets.checkRestrictions();
		});
	}
}
