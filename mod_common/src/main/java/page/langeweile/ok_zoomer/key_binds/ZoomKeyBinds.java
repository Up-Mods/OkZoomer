package page.langeweile.ok_zoomer.key_binds;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.utils.ModUtils;

// Manages the zoom key binds themselves
public class ZoomKeyBinds {
	// TODO - Bleh, immutability; I have a plan
	private static final boolean ENABLE_EXTRA_KEY_BINDS = OkZoomerConfigManager.CONFIG.controls.extraKeyBinds.getRealValue();

	// The "Zoom" category
	public static final KeyMapping.Category ZOOM_CATEGORY = KeyMapping.Category.register(ModUtils.id("zoom"));

	// The zoom key bind, which will be registered
	public static final KeyMapping ZOOM_KEY = new KeyMapping("key.ok_zoomer.zoom", GLFW.GLFW_KEY_C, ZOOM_CATEGORY);

	// The "Decrease Zoom" key bind
	public static final KeyMapping DECREASE_ZOOM_KEY = getExtraKeyBind("key.ok_zoomer.decrease_zoom");

	// The "Increase Zoom" key bind
	public static final KeyMapping INCREASE_ZOOM_KEY = getExtraKeyBind("key.ok_zoomer.increase_zoom");

	// The "Reset Zoom" key bind
	public static final KeyMapping RESET_ZOOM_KEY = getExtraKeyBind("key.ok_zoomer.reset_zoom");

	// The method used to check if the zoom manipulation key binds should be disabled, can be used by other mods.
	public static boolean areExtraKeyBindsEnabled() {
		return ZoomKeyBinds.ENABLE_EXTRA_KEY_BINDS;
	}

	// The method used to get the extra keybinds, if disabled, return null.
	public static KeyMapping getExtraKeyBind(String translationKey) {
		if (ZoomKeyBinds.areExtraKeyBindsEnabled()) {
			return new KeyMapping(translationKey, InputConstants.UNKNOWN.getValue(), ZOOM_CATEGORY);
		}

		return null;
	}
}
