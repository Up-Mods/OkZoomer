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

//TODO - Split the zoom management from the keybind management.
//This class is responsible for the management of the zoom divisor and of the keybinds.
public class OkZoomerMod implements ClientModInitializer {
	protected static final Logger modLogger = LogManager.getFormatterLogger("Ok Zoomer Next");
	
	public static final int getDefaultKey() {
		//If OptiFabric (and therefore, OptiFine) is detected, use Z as the default value instead.
		if (FabricLoader.getInstance().isModLoaded("optifabric")) {
			modLogger.info("[Ok Zoomer Next] OptiFabric was detected! Using Z as the default key.");
			return GLFW.GLFW_KEY_Z;
		} else {
			return GLFW.GLFW_KEY_C;
		}
	}

	//The zoom keybinding, which will be registered.
	public static final FabricKeyBinding zoomKeyBinding = FabricKeyBinding.Builder
		.create(new Identifier("okzoomer", "zoom"), InputUtil.Type.KEYSYM, getDefaultKey(), "key.okzoomer.category")
		.build();

	//The "Decrease Zoom" keybinding.
	public static final FabricKeyBinding decreaseZoomKeyBinding = FabricKeyBinding.Builder
		.create(new Identifier("okzoomer", "decrease_zoom"), InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "key.okzoomer.category")
		.build();

	//The "Increase Zoom" keybinding.
	public static final FabricKeyBinding increaseZoomKeyBinding = FabricKeyBinding.Builder
		.create(new Identifier("okzoomer", "increase_zoom"), InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "key.okzoomer.category")
		.build();

	//The zoom signal, which is managed in an event and used by other mixins.
	public static boolean isZoomKeyPressed = false;

	//Used internally in order to make zoom toggling possible.
	private static boolean previousZoomPress = false;

	//Used for post-zoom actions like updating the terrain.
	public static boolean zoomHasHappened = false;

	//The zoom divisor, managed by the zoom press and zoom scrolling. Used by other mixins.
	public static double zoomDivisor = OkZoomerConfig.zoomDivisor.getValue();

	public static void changeZoomDivisor(boolean increase) {
		if (increase) {
			if (zoomDivisor < OkZoomerConfig.maximumZoomDivisor.getValue()) {
				zoomDivisor += 0.5D;
			} else {
				zoomDivisor = OkZoomerConfig.maximumZoomDivisor.getValue();
			}
		} else {
			if (zoomDivisor > OkZoomerConfig.minimumZoomDivisor.getValue()) {
				zoomDivisor -= 0.5D;
				zoomHasHappened = true;
			} else {
				zoomDivisor = OkZoomerConfig.minimumZoomDivisor.getValue();
			}
		}
	}

	@Override
	public void onInitializeClient() {
		//TODO - Actually do zoom stuff, remove when everything's done.
		modLogger.info("[Ok Zoomer Next] owo what's this");

		//Register the zoom category.
		KeyBindingRegistry.INSTANCE.addCategory("key.okzoomer.category");
		//Register the zoom keybinding.
		KeyBindingRegistry.INSTANCE.register(zoomKeyBinding);
		//Register the "Decrease Zoom" keybinding.
		KeyBindingRegistry.INSTANCE.register(decreaseZoomKeyBinding);
		//Register the "Increase Zoom" keybinding.
		KeyBindingRegistry.INSTANCE.register(increaseZoomKeyBinding);

		//This event is responsible for managing the zoom signal.
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

			//Manage the post-zoom signal.
			if (!isZoomKeyPressed && previousZoomPress) {
				zoomHasHappened = true;
			} else {
				zoomHasHappened = false;
			}

			//Set the previous zoom signal for the next tick.
			previousZoomPress = zoomKeyBinding.isPressed();
		});
		
		ClientTickCallback.EVENT.register(e -> {
			if (decreaseZoomKeyBinding.isPressed()) {
				changeZoomDivisor(false);
			}

			if (increaseZoomKeyBinding.isPressed()) {
				changeZoomDivisor(true);
			}
		});
	}
}
