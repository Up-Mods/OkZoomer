package page.langeweile.ok_zoomer.mixin.forge.compat.embeddium;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import page.langeweile.ok_zoomer.zoom.Zoom;

// Hack necessary because Embeddium can't steal Sodium code anymore but I can steal my own old code!
// Do not do this at home, kids; I know what I'm doing here
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer")
public abstract class SodiumWorldRendererMixin {
	@Unique
	private int prevZoomDivisor = 1;

	@ModifyVariable(
		method = "setupTerrain",
		at = @At(value = "STORE"),
		ordinal = 2
	)
	private boolean accountForZooming(boolean original, @Share("zoomDivisor") LocalIntRef zoomDivisor) {
		zoomDivisor.set(Zoom.isZooming() ? Mth.floor(Zoom.getZoomDivisor()) : 1);
		return original || zoomDivisor.get() != this.prevZoomDivisor;
	}

	@Inject(
		method = "setupTerrain",
		at = @At(
			value = "FIELD",
			target = "Lme/jellysquid/mods/sodium/client/render/SodiumWorldRenderer;lastCameraX:D",
			ordinal = 1
		)
	)
	private void storeLastDivisor(CallbackInfo ci, @Share("zoomDivisor") LocalIntRef zoomDivisor) {
		this.prevZoomDivisor = zoomDivisor.get();
	}
}
