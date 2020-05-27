package io.github.joaoh1.okzoomer.mixin;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.joaoh1.okzoomer.OkZoomerMod;
import io.github.joaoh1.okzoomer.config.OkZoomerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.util.InputUtil;

//TODO - Create a mixin plugin for this, this should only be injected once and then never more.
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	private static MinecraftClient instance;

	@Shadow
	public final GameOptions options;

	@Shadow
	public final File runDirectory;

	private static final Logger modLogger = LogManager.getFormatterLogger("Ok Zoomer Next");

	public MinecraftClientMixin(RunArgs args) {
		this.runDirectory = args.directories.runDir;
		this.options = new GameOptions(instance, this.runDirectory);
	}

	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/client/RunArgs;)V")
	public void hijackCKeybind(RunArgs args, CallbackInfo info) {
		//Load the configuration.
		OkZoomerConfig.loadJanksonConfig();

		//If "Unbind Conflicting Keybind" is true, unbind the "Save Toolbar Activator" keybind if it hasn't been changed.
		if (OkZoomerConfig.unbindConflictingKeybind.getValue()) {
			if (OkZoomerMod.zoomKeyBinding.isDefault()) {
				if (this.options.keySaveToolbarActivator.isDefault()) {
					modLogger.info("[Ok Zoomer Next] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding... This process won't be repeated.");
					this.options.keySaveToolbarActivator.setBoundKey(InputUtil.fromKeyCode(InputUtil.UNKNOWN_KEY.getCode(), InputUtil.UNKNOWN_KEY.getCode()));
				}
			}
			//Set self to false.
			OkZoomerConfig.unbindConflictingKeybind.setValue(false);
			OkZoomerConfig.saveJanksonConfig();
		}
	}
}