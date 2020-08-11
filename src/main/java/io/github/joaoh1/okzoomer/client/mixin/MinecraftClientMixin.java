package io.github.joaoh1.okzoomer.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.joaoh1.okzoomer.client.packets.ZoomPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

//TODO - Move this to Fabric API once the bikeshed is done.
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Inject(
		at = @At("TAIL"),
		method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V"
	)
	public void resetZoomLimitations(Screen screen, CallbackInfo info) {
		ZoomPackets.disableZoom = false;
		ZoomPackets.disableZoomScrolling = false;
	}
}