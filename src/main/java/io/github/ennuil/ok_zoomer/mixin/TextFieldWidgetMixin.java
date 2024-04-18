package io.github.ennuil.ok_zoomer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.ennuil.ok_zoomer.config.screen.widgets.LabelledTextFieldWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin {
	@ModifyExpressionValue(
		method = "drawWidget",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;getY()I"
		)
	)
	private int modifyGetY(int original) {
		return (TextFieldWidget) (Object) this instanceof LabelledTextFieldWidget ? original + 12 : original;
	}

	@ModifyExpressionValue(
		method = "drawWidget",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;height:I"
		)
	)
	private int modifyHeight(int original) {
		return (TextFieldWidget) (Object) this instanceof LabelledTextFieldWidget ? original - 12 : original;
	}
}
