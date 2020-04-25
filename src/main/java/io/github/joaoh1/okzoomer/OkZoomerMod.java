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
	protected static Logger okZoomerLogger = LogManager.getFormatterLogger("Ok Zoomer");
	
	public static int getDefaultKey() {
		//If OptiFabric (and therefore, OptiFine) is detected, use Z as the default value instead.
		if (FabricLoader.getInstance().isModLoaded("optifabric")) {
			okZoomerLogger.info("OptiFabric was detected! Using Z as the default key.");
			return GLFW.GLFW_KEY_Z;
		} else {
			return GLFW.GLFW_KEY_C;
		}
	}

	//The zoom keybinding, which will be registered.
	public static final FabricKeyBinding zoomKeyBinding = FabricKeyBinding.Builder
		.create(new Identifier("okzoomer", "zoom"), InputUtil.Type.KEYSYM, getDefaultKey(), "key.categories.misc")
		.build();

	//The zoom signal, which is managed in an event and used by other mixins.
	public static boolean isZoomKeyPressed = false;

	//Used internally in order to make zoom toggling possible.
	private static boolean previousZoomPress = false;

	@Override
	public void onInitializeClient() {
		// TODO - Actually do zoom stuff, remove when everything's done.
		okZoomerLogger.info("owo what's this");

		// Load the configuration.
		OkZoomerConfig.loadJanksonConfig();

		// Register the zoom keybinding.
		KeyBindingRegistry.INSTANCE.register(zoomKeyBinding);

		// This event is responsible for managing the zoom signal.
		ClientTickCallback.EVENT.register(e -> {
			//If the press state is the same as the previous tick's, cancel the rest. Makes toggling usable.
			if (zoomKeyBinding.isPressed() == previousZoomPress) return;

			if (!OkZoomerConfig.zoomToggle.getValue()) {
				//If zoom toggling is disabled, then the zoom signal is determined by if the key is pressed or not.
				isZoomKeyPressed = zoomKeyBinding.isPressed();
			} else {
				//If zoom toggling is enabled, toggle the zoom signal instead.
				if (zoomKeyBinding.isPressed()) {
					isZoomKeyPressed = !isZoomKeyPressed;
				}
			}

			//Set the previous zoom signal for the next tick.
			previousZoomPress = zoomKeyBinding.isPressed();
		});
	}
}
