package page.langeweile.ok_zoomer.mixin.common.key_binds;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.config.screen.OkZoomerConfigScreen;
import page.langeweile.ok_zoomer.utils.ModUtils;

@Mixin(KeyBindsList.KeyEntry.class)
public abstract class KeyEntryMixin extends KeyBindsList.Entry {
	@Unique
	private static final WidgetSprites SETTINGS_BUTTON_SPRITE = new WidgetSprites(
		ModUtils.id("key_binds/settings"),
		ModUtils.id("key_binds/settings_selected")
	);

	@Unique
	private Button settingsButton;

	@Shadow(remap = false, aliases = "this$0")
	@Final
	KeyBindsList field_2742;

	@Shadow
	@Final
	private Button changeButton;

	@Shadow
	@Final
	private Component name;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initSettingsButton(KeyBindsList keyBindsList, KeyMapping key, Component name, CallbackInfo ci) {
		if (key.getName().equals("key.ok_zoomer.zoom") && OkZoomerConfigManager.CONFIG.tweaks.showSettingsOnKey.value()) {
			this.settingsButton = new ImageButton(
				0, 0, 20, 20,
				SETTINGS_BUTTON_SPRITE,
				button -> ((AbstractSelectionListAccessor) field_2742).getMinecraft().setScreen(
					new OkZoomerConfigScreen(((KeyBindsListAccessor) field_2742).getKeyBindsScreen())
				),
				Component.translatable("key.ok_zoomer.settings", this.name));
		}
	}

	@Inject(method = "renderContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"))
	private void renderSettingsButton(GuiGraphics graphics, int mouseX, int mouseY, boolean bl, float partialTick, CallbackInfo ci) {
		if (this.settingsButton != null) {
			this.settingsButton.setPosition(this.changeButton.getX() - 25, this.getContentY() - 2);
			this.settingsButton.render(graphics, mouseX, mouseY, partialTick);
		}
	}

	@WrapOperation(method = { "children", "narratables" }, at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<?> addSettingsButtonToLists(Object e1, Object e2, Operation<ImmutableList<?>> original) {
		if (this.settingsButton != null) {
			return ImmutableList.of(this.settingsButton, e1, e2);
		} else {
			return original.call(e1, e2);
		}
	}
}
