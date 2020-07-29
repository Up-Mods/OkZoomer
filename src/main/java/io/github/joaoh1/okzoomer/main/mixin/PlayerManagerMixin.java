package io.github.joaoh1.okzoomer.main.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.joaoh1.okzoomer.client.utils.ZoomUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@Inject(at = @At("RETURN"), method = "onPlayerConnect")
	private void sendModeInfo(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
		if (true) {
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			//ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ZoomUtils.DISABLE_ZOOM_SCROLLING_PACKET_ID, passedData);
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ZoomUtils.FORCE_CLASSIC_PRESET_PACKET_ID, passedData);
			//ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, ZoomUtils.DISABLE_ZOOM_PACKET_ID, passedData);
		}
	}
}