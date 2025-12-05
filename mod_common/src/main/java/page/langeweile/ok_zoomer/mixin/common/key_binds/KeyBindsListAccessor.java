package page.langeweile.ok_zoomer.mixin.common.key_binds;

import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBindsList.class)
public interface KeyBindsListAccessor {
	@Accessor
	KeyBindsScreen getKeyBindsScreen();
}
