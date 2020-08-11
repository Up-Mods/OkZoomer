package io.github.joaoh1.okzoomer.client;

import java.util.Random;

import io.github.joaoh1.okzoomer.client.config.OkZoomerConfig;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.ZoomModes;
import io.github.joaoh1.okzoomer.client.utils.ZoomUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;

//This class is responsible for the management of the zoom divisor and of the keybinds.
public class OkZoomerClientMod implements ClientModInitializer {
	//The zoom keybinding, which will be registered.
	public static final KeyBinding zoomKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding("key.okzoomer.zoom", InputUtil.Type.KEYSYM, ZoomUtils.getDefaultZoomKey(), "key.okzoomer.category"));
	
	//The "Decrease Zoom" keybinding.
	public static final KeyBinding decreaseZoomKeyBinding = ZoomUtils.getExtraKeybind("key.okzoomer.decrease_zoom");

	//The "Increase Zoom" keybinding.
	public static final KeyBinding increaseZoomKeyBinding = ZoomUtils.getExtraKeybind("key.okzoomer.increase_zoom");

	//The "Reset Zoom" keybinding.
	public static final KeyBinding resetZoomKeyBinding = ZoomUtils.getExtraKeybind("key.okzoomer.reset_zoom");

	//Used internally in order to make zoom toggling possible.
	private static boolean lastZoomPress = false;

	//Used internally in order to make persistent zoom less buggy.
	private static boolean persistentZoom = false;

	@Override
	public void onInitializeClient() {
		//TODO - Actually do zoom stuff, remove when everything's done.
		Random random = new Random();
		String[] owo = new String[]{"owo", "OwO", "uwu", "nwn", "^w^", ">w<", "Owo", "owO", ";w;", "0w0", "QwQ", "TwT", "-w-", "$w$", "@w@", "*w*", ":w:", "°w°", "ºwº", "ówò", "òwó", "`w´", "´w`", "~w~", "umu", "nmn", "own", "nwo", "ùwú", "úwù", "ñwñ", "UwU", "NwN", "ÙwÚ", "PwP", "own", "nwo", "/w/", "\\w\\", "|w|", "#w#", "<>w<>", "'w'", "\"w\"", "öwö", "ôwô", "ÖwÖ", "ÔwÔ", ".w.", "+w+", ")w(", "]w[", "}w{"};
		ZoomUtils.modLogger.info("[Ok Zoomer] " + owo[random.nextInt(owo.length)] + " what's this");

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			//Attempt to load the config if it hasn't been loaded yet, which is unlikely due to extra keybinds.
			if (!OkZoomerConfig.isConfigLoaded) {
				OkZoomerConfig.loadModConfig();
			}

			//This handles the hijacking of the "Save Toolbar Activator" key.
			if (OkZoomerConfigPojo.tweaks.unbindConflictingKey) {
				ZoomUtils.unbindConflictingKey(client, false);
				OkZoomerConfigPojo.tweaks.unbindConflictingKey = false;
				OkZoomerConfig.saveModConfig();
			}
		});

		//This event is responsible for managing the zoom signal.
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
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
		
		if (ZoomUtils.areExtraKeybindsEnabled()) {
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
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
					ZoomUtils.resetZoomDivisor();
				}
			});
		}
		
		ClientSidePacketRegistry.INSTANCE.register(ZoomUtils.DISABLE_ZOOM_PACKET_ID,
            (packetContext, attachedData) -> packetContext.getTaskQueue().execute(() -> {
				MinecraftClient client = MinecraftClient.getInstance();
				client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT, new TranslatableText("toast.okzoomer.title"), new TranslatableText("toast.okzoomer.disable_zoom")));
				ZoomUtils.disableZoom = true;
			})
		);

		ClientSidePacketRegistry.INSTANCE.register(ZoomUtils.DISABLE_ZOOM_SCROLLING_PACKET_ID,
            (packetContext, attachedData) -> packetContext.getTaskQueue().execute(() -> {
				MinecraftClient client = MinecraftClient.getInstance();
				client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT, new TranslatableText("toast.okzoomer.title"), new TranslatableText("toast.okzoomer.disable_zoom_scrolling")));
				ZoomUtils.disableZoomScrolling = true;
			})
		);
	}
}
