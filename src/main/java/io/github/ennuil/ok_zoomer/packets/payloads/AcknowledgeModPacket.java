package io.github.ennuil.ok_zoomer.packets.payloads;

import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public record AcknowledgeModPacket(boolean unrestricted) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, AcknowledgeModPacket> STREAM_CODEC = CustomPacketPayload.codec(AcknowledgeModPacket::write, AcknowledgeModPacket::new);
	public static final CustomPacketPayload.Type<AcknowledgeModPacket> TYPE = CustomPacketPayload.createType("ok_zoomer:acknowledge_mod");

	public AcknowledgeModPacket(FriendlyByteBuf buffer) {
		this(buffer.readBoolean());
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(this.unrestricted());
	}

	@ClientOnly
	public static void handle(AcknowledgeModPacket payload, ClientConfigurationNetworking.Context context) {
		ZoomPackets.checkRestrictions();
		if (payload.unrestricted()) {
			if (ZoomPackets.getAcknowledgement().equals(ZoomPackets.Acknowledgement.HAS_NO_RESTRICTIONS)) {
				ZoomUtils.LOGGER.info("[Ok Zoomer] This server acknowledges the mod and establishes no restrictions");
				ZoomPackets.sendToast(Component.translatable("toast.ok_zoomer.acknowledge_mod"));
			}
		} else {
			if (ZoomPackets.getAcknowledgement().equals(ZoomPackets.Acknowledgement.HAS_RESTRICTIONS)) {
				ZoomUtils.LOGGER.info("[Ok Zoomer] This server acknowledges the mod and has established some restrictions");
				ZoomPackets.sendToast(Component.translatable("toast.ok_zoomer.acknowledge_mod_restrictions"));
			}
		}
	}
}
