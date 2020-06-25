package io.github.joaoh1.okzoomer.client;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.ZoomModes;
import io.github.joaoh1.okzoomer.client.utils.ZoomUtils;
import io.github.joaoh1.okzoomer.main.OkZoomerMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
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
	private static boolean previousZoomPress = false;

	//Used internally in order to make persistent zoom less buggy.
	private static boolean persistentZoomEnabled = false;

	@Override
	public void onInitializeClient() {
		//TODO - Actually do zoom stuff, remove when everything's done.
		Random random = new Random();
		String[] owo = new String[]{"owo", "OwO", "uwu", "nwn", "^w^", ">w<", "Owo", "owO", ";w;", "0w0", "QwQ", "TwT", "-w-", "$w$", "@w@", "*w*", ":w:", "°w°", "ºwº", "ówò", "òwó", "`w´", "´w`", "~w~", "umu", "nmn", "own", "nwo", "ùwú", "úwù", "ñwñ", "UwU", "NwN", "ÙwÚ", "PwP", "<>w<>"};
		modLogger.info("[Ok Zoomer Next] " + owo[random.nextInt(owo.length)] + " what's this");

		//This event is responsible for managing the zoom signal.
		ClientTickCallback.EVENT.register(e -> {
			//If zoom is disabled, do not allow for zooming at all.
			if (ZoomUtils.isZoomDisabled) {
				return;
			}

			if (!OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.HOLD)) {
				if (!persistentZoomEnabled) {
					persistentZoomEnabled = true;
					previousZoomPress = true;
					ZoomUtils.zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
				}
			} else {
				if (persistentZoomEnabled) {
					persistentZoomEnabled = false;
					previousZoomPress = true;
				}
			}

			//If the press state is the same as the previous tick's, cancel the rest. Makes toggling usable and the zoom divisor adjustable.
			if (zoomKeyBinding.isPressed() == previousZoomPress) {
				return;
			}

			if (OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.HOLD)) {
				//If zoom toggling is disabled, then the zoom signal is determined by if the key is pressed or not.
				ZoomUtils.isZoomKeyPressed = zoomKeyBinding.isPressed();
				ZoomUtils.zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
			} else if (OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.TOGGLE)) {
				//If zoom toggling is enabled, toggle the zoom signal instead.
				if (zoomKeyBinding.isPressed()) {
					ZoomUtils.isZoomKeyPressed = !ZoomUtils.isZoomKeyPressed;
					ZoomUtils.zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
				}
			} else if (OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.PERSISTENT)) {
				//If persistent zoom is enabled, just keep the zoom on.
				ZoomUtils.isZoomKeyPressed = true;
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

			if (resetZoomKeyBinding.isPressed()) {
				ZoomUtils.zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
			}
		});
		
		ClientSidePacketRegistry.INSTANCE.register(OkZoomerMod.FORCE_OPTIFINE_MODE_PACKET_ID,
            (packetContext, attachedData) -> packetContext.getTaskQueue().execute(() -> {
				packetContext.getPlayer().sendMessage(new LiteralText(":crab: boomer mode is on :crab:"), false);
				System.out.println("it worked!");
			})
		);

		ClientSidePacketRegistry.INSTANCE.register(OkZoomerMod.DISABLE_ZOOMING_PACKET_ID,
            (packetContext, attachedData) -> packetContext.getTaskQueue().execute(() -> {
				packetContext.getPlayer().sendMessage(new LiteralText("Ok Zoomer has been disabled by this server."), false);
				ZoomUtils.isZoomDisabled = true;
			})
		);
	}
}
