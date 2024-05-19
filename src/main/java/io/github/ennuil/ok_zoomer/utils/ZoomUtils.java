package io.github.ennuil.ok_zoomer.utils;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import io.github.ennuil.ok_zoomer.OkZoomerClientMod;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.key_binds.ZoomKeyBinds;
import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import org.quiltmc.qsl.tag.api.QuiltTagKey;
import org.quiltmc.qsl.tag.api.TagType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// The class that contains most of the logic behind the zoom itself
public class ZoomUtils {
	// The logger, used everywhere to print messages to the console
	public static final Logger LOGGER = LoggerFactory.getLogger("Ok Zoomer");

	public static final ZoomInstance ZOOMER_ZOOM = new ZoomInstance(
		ZoomUtils.id("zoom"),
		4.0F,
		new SmoothTransitionMode(0.75f),
		new ZoomDivisorMouseModifier(),
		null
	);

	public static final SystemToast.SystemToastId TOAST_ID = new SystemToast.SystemToastId();

	public static final TagKey<Item> ZOOM_DEPENDENCIES_TAG = QuiltTagKey.of(Registries.ITEM, ZoomUtils.id("zoom_dependencies"), TagType.CLIENT_FALLBACK);

	public static int zoomStep = 0;

	private static boolean openCommandScreen = false;

	// The method used for changing the zoom divisor, used by zoom scrolling and the key binds
	public static void changeZoomDivisor(boolean increase) {
		//If the zoom is disabled, don't allow for zoom scrolling
		if (ZoomPackets.shouldDisableZoom()) return;

		double zoomDivisor = OkZoomerConfigManager.CONFIG.zoomValues.zoomDivisor.value();
		double minimumZoomDivisor = OkZoomerConfigManager.CONFIG.zoomValues.minimumZoomDivisor.value();
		double maximumZoomDivisor = OkZoomerConfigManager.CONFIG.zoomValues.maximumZoomDivisor.value();
		int upperScrollStep = OkZoomerConfigManager.CONFIG.zoomValues.upperScrollSteps.value();
		int lowerScrollStep = OkZoomerConfigManager.CONFIG.zoomValues.lowerScrollSteps.value();

		if (ZoomPackets.shouldForceZoomDivisors()) {
			minimumZoomDivisor = Math.max(minimumZoomDivisor, ZoomPackets.getMinimumZoomDivisor());
			maximumZoomDivisor = Math.min(maximumZoomDivisor, ZoomPackets.getMaximumZoomDivisor());
		}

		zoomStep = increase ? Math.min(zoomStep + 1, upperScrollStep) :  Math.max(zoomStep - 1, -lowerScrollStep);

		if (zoomStep > 0) {
			ZOOMER_ZOOM.setZoomDivisor(zoomDivisor + ((maximumZoomDivisor - zoomDivisor) / upperScrollStep * zoomStep));
		} else if (zoomStep == 0) {
			ZOOMER_ZOOM.setZoomDivisor(zoomDivisor);
		} else {
			ZOOMER_ZOOM.setZoomDivisor(zoomDivisor + ((minimumZoomDivisor - zoomDivisor) / lowerScrollStep * -zoomStep));
		}
	}

	// The method used by both the "Reset Zoom" keybind and the "Reset Zoom With Mouse" tweak
	public static void resetZoomDivisor(boolean userPrompted) {
		if (userPrompted && ZoomPackets.shouldDisableZoom()) return;
		if (!userPrompted && !OkZoomerConfigManager.CONFIG.tweaks.forgetZoomDivisor.value()) return;

		ZOOMER_ZOOM.resetZoomDivisor();
		zoomStep = 0;
	}

	public static void keepZoomStepsWithinBounds() {
		int upperScrollStep = OkZoomerConfigManager.CONFIG.zoomValues.upperScrollSteps.value();
		int lowerScrollStep = OkZoomerConfigManager.CONFIG.zoomValues.lowerScrollSteps.value();

		zoomStep = Mth.clamp(zoomStep, -lowerScrollStep, upperScrollStep);
	}

	// The method used for unbinding the "Save Toolbar Activator"
	public static void unbindConflictingKey(Minecraft client, boolean userPrompted) {
		if (ZoomKeyBinds.ZOOM_KEY.isDefault()) {
			if (client.options.keySaveHotbarActivator.isDefault()) {
				if (userPrompted) {
					ZoomUtils.LOGGER.info("[Ok Zoomer] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding...");
					client.getToasts().addToast(SystemToast.multiline(
						client, TOAST_ID, Component.translatable("toast.ok_zoomer.title"),
						Component.translatable("toast.ok_zoomer.unbind_conflicting_key.success")));
				} else {
					ZoomUtils.LOGGER.info("[Ok Zoomer] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding... This process won't be repeated until specified in the config.");
				}
				client.options.keySaveHotbarActivator.setKey(InputConstants.UNKNOWN);
				client.options.save();
				KeyMapping.resetMapping();
			} else {
				ZoomUtils.LOGGER.info("[Ok Zoomer] No conflicts with the \"Save Toolbar Activator\" keybind were found!");
				if (userPrompted) {
					client.getToasts().addToast(SystemToast.multiline(
						client, TOAST_ID, Component.translatable("toast.ok_zoomer.title"),
						Component.translatable("toast.ok_zoomer.unbind_conflicting_key.no_conflict")));
				}
			}
		}
	}

	public static boolean shouldOpenCommandScreen() {
		return openCommandScreen;
	}

	public static void setOpenCommandScreen(boolean openCommandScreen) {
		ZoomUtils.openCommandScreen = openCommandScreen;
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(OkZoomerClientMod.MODID, path);
	}
}
