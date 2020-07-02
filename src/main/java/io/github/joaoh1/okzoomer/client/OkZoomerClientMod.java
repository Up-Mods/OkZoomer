package io.github.joaoh1.okzoomer.client;

import java.nio.file.Files;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import io.github.joaoh1.okzoomer.client.config.OkZoomerConfig;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.ZoomModes;
import io.github.joaoh1.okzoomer.client.utils.ZoomUtils;
import io.github.joaoh1.okzoomer.main.OkZoomerMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;

//This class is responsible for the management of the zoom divisor and of the keybinds.
public class OkZoomerClientMod implements ClientModInitializer {
	//The logger, used here to owo, because why not?
	protected static final Logger modLogger = LogManager.getFormatterLogger("Ok Zoomer Next");
	
	//The zoom keybinding, which will be registered.
	public static final KeyBinding zoomKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding("key.okzoomer.zoom", InputUtil.Type.KEYSYM, ZoomUtils.getDefaultZoomKey(), "key.okzoomer.category"));

	//The "Decrease Zoom" keybinding.
	public static final KeyBinding decreaseZoomKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding("key.okzoomer.decrease_zoom", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "key.okzoomer.category"));

	//The "Increase Zoom" keybinding.
	public static final KeyBinding increaseZoomKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding("key.okzoomer.increase_zoom", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "key.okzoomer.category"));

	//The "Reset Zoom" keybinding.
	public static final KeyBinding resetZoomKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding("key.okzoomer.reset_zoom", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "key.okzoomer.category"));

	//Used internally in order to make zoom toggling possible.
	private static boolean lastZoomPress = false;

	//Used internally in order to make persistent zoom less buggy.
	private static boolean persistentZoom = false;

	@Override
	public void onInitializeClient() {
		//TODO - Actually do zoom stuff, remove when everything's done.
		Random random = new Random();
		String[] owo = new String[]{"owo", "OwO", "uwu", "nwn", "^w^", ">w<", "Owo", "owO", ";w;", "0w0", "QwQ", "TwT", "-w-", "$w$", "@w@", "*w*", ":w:", "°w°", "ºwº", "ówò", "òwó", "`w´", "´w`", "~w~", "umu", "nmn", "own", "nwo", "ùwú", "úwù", "ñwñ", "UwU", "NwN", "ÙwÚ", "PwP", "own", "nwo", "/w/", "\\w\\", "|w|", "#w#", "<>w<>"};
		modLogger.info("[Ok Zoomer Next] " + owo[random.nextInt(owo.length)] + " what's this");

		//Handle the hijacking of the "Save Toolbar Activator" keybind's key.
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			//If the configuration didn't exist before, unbind the "Save Toolbar Activator" keybind if there's a conflict.
			if (!Files.exists(OkZoomerConfig.configPath)) {
				if (OkZoomerClientMod.zoomKeyBinding.isDefault() && ZoomUtils.getDefaultZoomKey() == GLFW.GLFW_KEY_C) {
					if (client.options.keySaveToolbarActivator.isDefault()) {
						modLogger.info("[Ok Zoomer Next] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding... This process won't be repeated.");
						client.options.keySaveToolbarActivator.setBoundKey(InputUtil.UNKNOWN_KEY);
						client.options.write();
						KeyBinding.updateKeysByCode();
					}
				}
			}

			//Load the config file, create new one if not found.
			OkZoomerConfig.loadModConfig();
		});

		//This event is responsible for managing the zoom signal.
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			//If zoom is disabled, do not allow for zooming at all.
			if (ZoomUtils.disableZoom) {
				return;
			}

			//Handle zoom mode changes.
			if (!OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.HOLD)) {
				if (!persistentZoom) {
					persistentZoom = true;
					lastZoomPress = true;
					ZoomUtils.zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
				}
			} else {
				if (persistentZoom) {
					persistentZoom = false;
					lastZoomPress = true;
				}
			}

			//If the press state is the same as the previous tick's, cancel the rest. Makes toggling usable and the zoom divisor adjustable.
			if (zoomKeyBinding.isPressed() == lastZoomPress) {
				return;
			}

			if (OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.HOLD)) {
				//If the zoom needs to be held, then the zoom signal is determined by if the key is pressed or not.
				ZoomUtils.zoomState = zoomKeyBinding.isPressed();
				ZoomUtils.zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
			} else if (OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.TOGGLE)) {
				//If the zoom needs to be toggled, toggle the zoom signal instead.
				if (zoomKeyBinding.isPressed()) {
					ZoomUtils.zoomState = !ZoomUtils.zoomState;
					ZoomUtils.zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
				}
			} else if (OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.PERSISTENT)) {
				//If persistent zoom is enabled, just keep the zoom on.
				ZoomUtils.zoomState = true;
			}

			//Manage the post-zoom signal.
			if (!ZoomUtils.zoomState && lastZoomPress) {
				ZoomUtils.lastZoomState = true;
			} else {
				ZoomUtils.lastZoomState = false;
			}

			//Set the previous zoom signal for the next tick.
			lastZoomPress = zoomKeyBinding.isPressed();
		});
		
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (ZoomUtils.disableZoomScrolling) {
				return;
			};

			if (decreaseZoomKeyBinding.isPressed()) {
				ZoomUtils.changeZoomDivisor(false);
			}

			if (increaseZoomKeyBinding.isPressed()) {
				ZoomUtils.changeZoomDivisor(true);
			}

			if (resetZoomKeyBinding.isPressed()) {
				ZoomUtils.zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
			}
		});
		
		ClientSidePacketRegistry.INSTANCE.register(OkZoomerMod.FORCE_OPTIFINE_MODE_PACKET_ID,
            (packetContext, attachedData) -> packetContext.getTaskQueue().execute(() -> {
				packetContext.getPlayer().sendMessage(new LiteralText("[Ok Zoomer] The zoom has been forced to behave like OptiFine's zoom by this server."), false);
				ZoomUtils.optifineMode = true;
			})
		);
		
		ClientSidePacketRegistry.INSTANCE.register(OkZoomerMod.DISABLE_ZOOM_PACKET_ID,
            (packetContext, attachedData) -> packetContext.getTaskQueue().execute(() -> {
				packetContext.getPlayer().sendMessage(new LiteralText("[Ok Zoomer] The zoom has been disabled by this server."), false);
				ZoomUtils.disableZoom = true;
			})
		);
	}
}
