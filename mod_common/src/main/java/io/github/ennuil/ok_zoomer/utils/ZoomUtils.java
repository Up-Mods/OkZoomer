package io.github.ennuil.ok_zoomer.utils;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.ennuil.ok_zoomer.config.ConfigEnums;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.key_binds.ZoomKeyBinds;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

// The class that contains most of the logic behind the zoom itself
public class ZoomUtils {
	// The logger, used everywhere to print messages to the console
	public static final Logger LOGGER = LoggerFactory.getLogger("Ok Zoomer");

	public static final TagKey<Item> ZOOM_DEPENDENCIES_TAG = TagKey.create(Registries.ITEM, ModUtils.id("zoom_dependencies"));

	public static int zoomStep = 0;

	private static Predicate<LocalPlayer> hasSpyglass = player -> player.isCreative();
	private static boolean safeSmartOcclusion = false;

	// The method used for changing the zoom divisor, used by zoom scrolling and the key binds
	public static void changeZoomDivisor(boolean increase) {
		if (OkZoomerConfigManager.CONFIG.features.scrollingMode.value() == ConfigEnums.ScrollingModes.EXPONENTIAL) {
			int scrollBase = OkZoomerConfigManager.CONFIG.zoomValues.scrollBase.value();
			int scrollResolution = OkZoomerConfigManager.CONFIG.zoomValues.scrollResolution.value();
			int upperScrollStep = OkZoomerConfigManager.CONFIG.zoomValues.scrollStepLimit.value();
			int lowerScrollStep = 0;

			zoomStep = increase ? Math.min(zoomStep + 1, upperScrollStep) :  Math.max(zoomStep - 1, -lowerScrollStep);

			double divisor = 1.0;
			if (zoomStep != 0) {
				divisor = Math.pow(scrollBase, (double) zoomStep / scrollResolution);
				Zoom.setZoomDivisor(divisor);
			} else {
				Zoom.setZoomDivisor(1);
			}

			if (OkZoomerConfigManager.CONFIG.tweaks.debugScrolling.value()) {
				Minecraft.getInstance().player.displayClientMessage(Component.literal( zoomStep + " - " + divisor), true);
			}
		} else {
			double zoomDivisor = OkZoomerConfigManager.CONFIG.legacyScrollValues.zoomDivisor.value();
			double minimumZoomDivisor = OkZoomerConfigManager.CONFIG.legacyScrollValues.minimumZoomDivisor.value();
			double maximumZoomDivisor = OkZoomerConfigManager.CONFIG.legacyScrollValues.maximumZoomDivisor.value();
			int upperScrollStep = OkZoomerConfigManager.CONFIG.legacyScrollValues.upperScrollSteps.value();
			int lowerScrollStep = OkZoomerConfigManager.CONFIG.legacyScrollValues.lowerScrollSteps.value();

			zoomStep = increase ? Math.min(zoomStep + 1, upperScrollStep) :  Math.max(zoomStep - 1, -lowerScrollStep);

			if (zoomStep > 0) {
				Zoom.setZoomDivisor(zoomDivisor + ((maximumZoomDivisor - zoomDivisor) / upperScrollStep * zoomStep));
			} else if (zoomStep == 0) {
				Zoom.setZoomDivisor(zoomDivisor);
			} else {
				Zoom.setZoomDivisor(zoomDivisor + ((minimumZoomDivisor - zoomDivisor) / lowerScrollStep * -zoomStep));
			}
		}
	}

	// The method used by both the "Reset Zoom" keybind and the "Reset Zoom With Mouse" tweak
	public static void resetZoomDivisor(boolean userPrompted) {
		if (!userPrompted && !OkZoomerConfigManager.CONFIG.tweaks.forgetZoomDivisor.value()) return;

		if (OkZoomerConfigManager.CONFIG.features.scrollingMode.value() == ConfigEnums.ScrollingModes.EXPONENTIAL) {
			int scrollBase = OkZoomerConfigManager.CONFIG.zoomValues.scrollBase.value();
			int scrollResolution = OkZoomerConfigManager.CONFIG.zoomValues.scrollResolution.value();
			zoomStep = OkZoomerConfigManager.CONFIG.zoomValues.defaultScrollStep.value();
			Zoom.setZoomDivisor(Math.pow(scrollBase, (double) zoomStep / scrollResolution));
		} else {
			zoomStep = 0;
			Zoom.setZoomDivisor(OkZoomerConfigManager.CONFIG.legacyScrollValues.zoomDivisor.value());
		}
	}

	public static void keepZoomStepsWithinBounds() {
		boolean isExponential = OkZoomerConfigManager.CONFIG.features.scrollingMode.value() == ConfigEnums.ScrollingModes.EXPONENTIAL;
		int upperScrollStep = isExponential ? OkZoomerConfigManager.CONFIG.zoomValues.scrollStepLimit.value() : OkZoomerConfigManager.CONFIG.legacyScrollValues.upperScrollSteps.value();
		int lowerScrollStep = isExponential ? 0 : OkZoomerConfigManager.CONFIG.legacyScrollValues.lowerScrollSteps.value();

		zoomStep = Mth.clamp(zoomStep, -lowerScrollStep, upperScrollStep);
	}

	// The method used for unbinding the "Save Toolbar Activator"
	public static void unbindConflictingKey(Minecraft client, boolean userPrompted) {
		if (ZoomKeyBinds.ZOOM_KEY.isDefault()) {
			if (client.options.keySaveHotbarActivator.isDefault()) {
				if (userPrompted) {
					ZoomUtils.LOGGER.info("[Ok Zoomer] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding...");
					client.getToasts().addToast(SystemToast.multiline(
						client, SystemToast.SystemToastIds.TUTORIAL_HINT, Component.translatable("toast.ok_zoomer.title"),
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
						client, SystemToast.SystemToastIds.TUTORIAL_HINT, Component.translatable("toast.ok_zoomer.title"),
						Component.translatable("toast.ok_zoomer.unbind_conflicting_key.no_conflict")));
				}
			}
		}
	}

	public static boolean hasSpyglass(LocalPlayer player) {
		return ZoomUtils.hasSpyglass.test(player);
	}

	public static void addSpyglassProvider(Predicate<LocalPlayer> provider) {
		ZoomUtils.hasSpyglass = ZoomUtils.hasSpyglass.or(provider);
	}

	public static void enableSafeSmartOcclusion() {
		ZoomUtils.safeSmartOcclusion = true;
	}

	public static boolean hasSmartOcclusion() {
		return OkZoomerConfigManager.CONFIG.tweaks.smartOcclusion.value() && ZoomUtils.safeSmartOcclusion;
	}

	public static boolean canSeeDistantEntities() {
		return switch (OkZoomerConfigManager.CONFIG.tweaks.seeDistantEntities.value()) {
			case SAFE -> ZoomUtils.safeSmartOcclusion;
			case ON -> true;
			case OFF -> false;
		};
	}
}
