package io.github.ennuil.ok_zoomer.mixin.common;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@ClientOnly
@Mixin(EditBox.class)
public interface EditBoxAccessor {
	@Accessor
	Font getFont();
}
