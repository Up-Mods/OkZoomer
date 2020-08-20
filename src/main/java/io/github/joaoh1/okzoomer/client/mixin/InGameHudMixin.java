package io.github.joaoh1.okzoomer.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.ZoomTransitionOptions;
import io.github.joaoh1.okzoomer.client.utils.ZoomUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

//This mixin handles the zoom overlay.
@Mixin(InGameHud.class)
public class InGameHudMixin {
	private static final Identifier ZOOM_OVERLAY = new Identifier("okzoomer:textures/misc/zoom_overlay.png");

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private int scaledWidth;

	@Shadow
	private int scaledHeight;

	//Updates the zoom overlay's alpha each tick if it's enabled.
	@Inject(
		at = @At("HEAD"),
		method = "tick()V"
	)
	private void zoomOverlayAlphaTick(CallbackInfo info) {
		if (OkZoomerConfigPojo.features.zoomOverlay == true) {
			ZoomUtils.updateZoomOverlayAlpha();
		}
	}

	//This applies the zoom overlay itself.
	@Inject(
		at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerInventory.getArmorStack(I)Lnet/minecraft/item/ItemStack;"),
		method = "render(F)V"
	)
	public void injectZoomOverlay(float tickDelta, CallbackInfo info) {
		if (OkZoomerConfigPojo.features.zoomOverlay) {
			//If zoom transitions is on, apply the transition to the overlay.
			if (!OkZoomerConfigPojo.features.zoomTransition.equals(ZoomTransitionOptions.OFF)) {
				if (ZoomUtils.zoomFovMultiplier != 0.0F) {
					float transparency = MathHelper.lerp(tickDelta, ZoomUtils.lastZoomOverlayAlpha, ZoomUtils.zoomOverlayAlpha);
					this.renderZoomOverlay(transparency);
				}
			} else {
				//Else, just do a simple toggle on the overlay.
				if (ZoomUtils.zoomState) {
					this.renderZoomOverlay(1.0F);
				}
			}
		}
	}

	//Renders the zoom overlay.
	public void renderZoomOverlay(float f) {
		RenderSystem.disableAlphaTest();
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, f);
		this.client.getTextureManager().bindTexture(ZOOM_OVERLAY);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0D, (double)this.scaledHeight, -90.0D).texture(0.0F, 1.0F).next();
		bufferBuilder.vertex((double)this.scaledWidth, (double)this.scaledHeight, -90.0D).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex((double)this.scaledWidth, 0.0D, -90.0D).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(0.0D, 0.0D, -90.0D).texture(0.0F, 0.0F).next();
		tessellator.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.enableAlphaTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}