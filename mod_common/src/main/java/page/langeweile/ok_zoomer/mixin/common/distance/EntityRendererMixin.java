package page.langeweile.ok_zoomer.mixin.common.distance;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import page.langeweile.ok_zoomer.zoom.Zoom;

// TODO - Determine whenever I'm cooking here or not
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
	@ModifyExpressionValue(method = "extractShadow", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/EntityRenderState;distanceToCameraSq:D", opcode = Opcodes.GETFIELD))
	private double modifyShadowVisibility(double original) {
		return original * Zoom.getTransitionMode().getInternalMultiplier();
	}
}
