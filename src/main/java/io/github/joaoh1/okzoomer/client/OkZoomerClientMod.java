package io.github.joaoh1.okzoomer.client;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.joaoh1.okzoomer.client.config.OkZoomerConfig;
import io.github.joaoh1.okzoomer.client.utils.ZoomUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

//TODO - Split the zoom management from the keybind management.
//This class is responsible for the management of the zoom divisor and of the keybinds.
public class OkZoomerClientMod implements ClientModInitializer {
	//The logger, used here to owo, because why not?
	protected static final Logger modLogger = LogManager.getFormatterLogger("Ok Zoomer Next");
	
	//The zoom keybinding, which will be registered.
	public static final FabricKeyBinding zoomKeyBinding = FabricKeyBinding.Builder
		.create(new Identifier("okzoomer", "zoom"), InputUtil.Type.KEYSYM, ZoomUtils.getDefaultZoomKey(), "key.okzoomer.category")
		.build();

	//The "Decrease Zoom" keybinding.
	public static final FabricKeyBinding decreaseZoomKeyBinding = FabricKeyBinding.Builder
		.create(new Identifier("okzoomer", "decrease_zoom"), InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "key.okzoomer.category")
		.build();

	//The "Increase Zoom" keybinding.
	public static final FabricKeyBinding increaseZoomKeyBinding = FabricKeyBinding.Builder
		.create(new Identifier("okzoomer", "increase_zoom"), InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "key.okzoomer.category")
		.build();

	//Used internally in order to make zoom toggling possible.
	private static boolean previousZoomPress = false;

	@Override
	public void onInitializeClient() {
		//TODO - Actually do zoom stuff, remove when everything's done.
		Random random = new Random();
		String[] owo = new String[]{"owo", "OwO", "uwu", "nwn", "^w^", ">w<", "Owo", "owO", ";w;", "0w0"};
		modLogger.info("[Ok Zoomer Next] " + owo[random.nextInt(10)] + " what's this");

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
				ZoomUtils.isZoomKeyPressed = zoomKeyBinding.isPressed();
				ZoomUtils.zoomDivisor = OkZoomerConfig.zoomDivisor.getValue();
			} else {
				//If zoom toggling is enabled, toggle the zoom signal instead.
				if (zoomKeyBinding.isPressed()) {
					ZoomUtils.isZoomKeyPressed = !ZoomUtils.isZoomKeyPressed;
					ZoomUtils.zoomDivisor = OkZoomerConfig.zoomDivisor.getValue();
				}
			}

			//Manage the post-zoom signal.
			if (!ZoomUtils.isZoomKeyPressed && previousZoomPress) {
				ZoomUtils.zoomHasHappened = true;
			} else {
				ZoomUtils.zoomHasHappened = false;
			}

			//Set the previous zoom signal for the next tick.
			previousZoomPress = zoomKeyBinding.isPressed();
		});
		
		ClientTickCallback.EVENT.register(e -> {
			if (decreaseZoomKeyBinding.isPressed()) {
				ZoomUtils.changeZoomDivisor(false);
			}

			if (increaseZoomKeyBinding.isPressed()) {
				ZoomUtils.changeZoomDivisor(true);
			}
		});
	}
}
