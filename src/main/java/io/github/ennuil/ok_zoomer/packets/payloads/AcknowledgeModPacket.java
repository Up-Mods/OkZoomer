package io.github.ennuil.ok_zoomer.packets.payloads;

import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketSender;

public record AcknowledgeModPacket(boolean unrestricted) implements CustomPacketPayload {

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(this.unrestricted());
	}

	@Override
	public ResourceLocation id() {
		return ZoomPackets.ACKNOWLEDGE_MOD_PACKET_ID;
	}

	public static AcknowledgeModPacket fromPacket(FriendlyByteBuf buffer) {
		return new AcknowledgeModPacket(buffer.readBoolean());
	}

	@ClientOnly
	public static void handle(Minecraft client, ClientConfigurationPacketListenerImpl packetListener, AcknowledgeModPacket payload, PacketSender<CustomPacketPayload> sender) {
		client.execute(() -> {
			ZoomPackets.checkRestrictions();
			if (payload.unrestricted()) {
				if (ZoomPackets.getAcknowledgement().equals(ZoomPackets.Acknowledgement.HAS_NO_RESTRICTIONS)) {
					ZoomUtils.LOGGER.info("[Ok Zoomer] This server acknowledges the mod and establishes no restrictions");
					ZoomPackets.sendToast(client, Component.translatable("toast.ok_zoomer.acknowledge_mod"));
				}
			} else {
				if (ZoomPackets.getAcknowledgement().equals(ZoomPackets.Acknowledgement.HAS_RESTRICTIONS)) {
					ZoomUtils.LOGGER.info("[Ok Zoomer] This server acknowledges the mod and has established some restrictions");
					ZoomPackets.sendToast(client, Component.translatable("toast.ok_zoomer.acknowledge_mod_restrictions"));
				}
			}
		});
	}
}
