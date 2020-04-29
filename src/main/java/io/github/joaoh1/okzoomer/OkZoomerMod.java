package io.github.joaoh1.okzoomer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import io.github.joaoh1.okzoomer.config.OkZoomerConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class OkZoomerMod implements ClientModInitializer {
	protected static Logger modLogger = LogManager.getFormatterLogger("Ok Zoomer");
	
	public static int getDefaultKey() {
		//If OptiFabric (and therefore, OptiFine) is detected, use Z as the default value instead.
		if (FabricLoader.getInstance().isModLoaded("optifabric")) {
			modLogger.info("[Ok Zoomer] OptiFabric was detected! Using Z as the default key.");
			return GLFW.GLFW_KEY_Z;
		} else {
			return GLFW.GLFW_KEY_C;
		}
	}

	//The zoom keybinding, which will be registered.
	public static final FabricKeyBinding zoomKeyBinding = FabricKeyBinding.Builder
		.create(new Identifier("okzoomer", "zoom"), InputUtil.Type.KEYSYM, getDefaultKey(), "key.categories.misc")
		.build();

	//The "Decrease Zoom" keybinding.
	public static final FabricKeyBinding decreaseZoomKeyBinding = FabricKeyBinding.Builder
		.create(new Identifier("okzoomer", "decrease_zoom"), InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEYCODE.getKeyCode(), "key.categories.misc")
		.build();

	//The "Increase Zoom" keybinding.
	public static final FabricKeyBinding increaseZoomKeyBinding = FabricKeyBinding.Builder
		.create(new Identifier("okzoomer", "increase_zoom"), InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEYCODE.getKeyCode(), "key.categories.misc")
		.build();

	//The zoom signal, which is managed in an event and used by other mixins.
	public static boolean isZoomKeyPressed = false;

	//Used internally in order to make zoom toggling possible.
	private static boolean previousZoomPress = false;

	//Used for post-zoom actions like updating the terrain.
	public static boolean zoomHasHappened = false;

	//The zoom divisor, managed by the zoom press and zoom scrolling. Used by other mixins.
	public static double zoomDivisor = OkZoomerConfig.zoomDivisor.getValue();

	@Override
	public void onInitializeClient() {
		// TODO - Actually do zoom stuff, remove when everything's done.
		modLogger.info("[Ok Zoomer] owo what's this");

		// Load the configuration.
		OkZoomerConfig.loadJanksonConfig();

		// Register the zoom keybinding.
		KeyBindingRegistry.INSTANCE.register(zoomKeyBinding);
		KeyBindingRegistry.INSTANCE.register(decreaseZoomKeyBinding);
		KeyBindingRegistry.INSTANCE.register(increaseZoomKeyBinding);

		// This event is responsible for managing the zoom signal.
		ClientTickCallback.EVENT.register(e -> {
			//If the press state is the same as the previous tick's, cancel the rest. Makes toggling usable and the zoom divisor adjustable.
			if (zoomKeyBinding.isPressed() == previousZoomPress) {
				return;
			}

			if (!OkZoomerConfig.zoomToggle.getValue()) {
				//If zoom toggling is disabled, then the zoom signal is determined by if the key is pressed or not.
				isZoomKeyPressed = zoomKeyBinding.isPressed();
				zoomDivisor = OkZoomerConfig.zoomDivisor.getValue();
			} else {
				//If zoom toggling is enabled, toggle the zoom signal instead.
				if (zoomKeyBinding.isPressed()) {
					isZoomKeyPressed = !isZoomKeyPressed;
					zoomDivisor = OkZoomerConfig.zoomDivisor.getValue();
				}
			}

			if (!isZoomKeyPressed && previousZoomPress) {
				zoomHasHappened = true;
			} else {
				zoomHasHappened = false;
			}

			//Set the previous zoom signal for the next tick.
			previousZoomPress = zoomKeyBinding.isPressed();
		});

		//TODO - Add proper functionality for the keybinds
		ClientTickCallback.EVENT.register(e -> {
			if (decreaseZoomKeyBinding.wasPressed()) {
				if (zoomDivisor <= 0.5D) {
					zoomDivisor = 0.5D;
				} else {
					zoomDivisor -= 0.5D;
				}
			}

			if (increaseZoomKeyBinding.wasPressed()) {
				zoomDivisor += 1.0D;
			}
		});
	}
}
