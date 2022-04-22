package io.github.ennuil.ok_zoomer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import io.github.ennuil.ok_zoomer.platform.migration.warning.OkZoomerPMWScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@ModifyArg(
		method = "<init>(Lnet/minecraft/client/RunArgs;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"
		)
	)
	private Screen showPMWScreen(Screen titleScreen) {
		return new OkZoomerPMWScreen(titleScreen);
	}
}
