package io.github.ennuil.ok_zoomer.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@ClientOnly
@Mixin(TextFieldWidget.class)
public interface TextFieldWidgetAccessor {
	@Accessor
	TextRenderer getTextRenderer();
}
