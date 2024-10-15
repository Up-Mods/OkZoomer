package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.ennuil.ok_zoomer.config.screen.components.LabelledEditBox;
import net.minecraft.client.gui.components.EditBox;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
@Mixin(EditBox.class)
public abstract class EditBoxMixin {
	@ModifyExpressionValue(
		method = "renderWidget",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/EditBox;getY()I"
		)
	)
	private int modifyGetY(int original) {
		return (EditBox) (Object) this instanceof LabelledEditBox ? original + 12 : original;
	}

	@ModifyExpressionValue(
		method = "renderWidget",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/EditBox;getHeight()I"
		)
	)
	private int modifyHeight(int original) {
		return (EditBox) (Object) this instanceof LabelledEditBox ? original - 12 : original;
	}

	@ModifyExpressionValue(
		method = "renderWidget",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/gui/components/EditBox;height:I"
		)
	)
	private int modifyHeight2(int original) {
		return (EditBox) (Object) this instanceof LabelledEditBox ? original - 12 : original;
	}
}
