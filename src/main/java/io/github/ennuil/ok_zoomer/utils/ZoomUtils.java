package io.github.ennuil.ok_zoomer.utils;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.key_binds.ZoomKeyBinds;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

// The class that contains most of the logic behind the zoom itself
public class ZoomUtils {
	// The logger, used everywhere to print messages to the console
	public static final Logger LOGGER = LoggerFactory.getLogger("Ok Zoomer");

	public static final SystemToast.SystemToastId TOAST_ID = new SystemToast.SystemToastId();

	public static final TagKey<Item> ZOOM_DEPENDENCIES_TAG = TagKey.create(Registries.ITEM, ZoomUtils.id("zoom_dependencies"));

	public static final Predicate<ItemStack> IS_VALID_SPYGLASS = stack -> ClientTags.isInWithLocalFallback(ZoomUtils.ZOOM_DEPENDENCIES_TAG, stack.getItem());

	public static int lastZoomCullingFov = 70;

	public static int zoomStep = 0;

	private static boolean openCommandScreen = false;

	private static Predicate<LocalPlayer> hasSpyglass = player -> player.getInventory().contains(IS_VALID_SPYGLASS) || player.isCreative();

	public static void validateZoomCulling() {
		var minecraft = Minecraft.getInstance();
		validateZoomCulling(minecraft);
	}

	public static void validateZoomCulling(Minecraft minecraft) {
		int fov = minecraft.options.fov().get();
		int divisor = Zoom.isZooming() ? Mth.floor(Zoom.getZoomDivisor()) : 1;
		int zoomCullingFov = Math.ceilDiv(fov, divisor);

		if (zoomCullingFov != lastZoomCullingFov) {
			minecraft.levelRenderer.getSectionOcclusionGraph().invalidate();
		}

		lastZoomCullingFov = zoomCullingFov;
	}

	// The method used for changing the zoom divisor, used by zoom scrolling and the key binds
	public static void changeZoomDivisor(Minecraft minecraft, boolean increase) {
		double zoomDivisor = OkZoomerConfigManager.CONFIG.zoomValues.zoomDivisor.value();
		double minimumZoomDivisor = OkZoomerConfigManager.CONFIG.zoomValues.minimumZoomDivisor.value();
		double maximumZoomDivisor = OkZoomerConfigManager.CONFIG.zoomValues.maximumZoomDivisor.value();
		int upperScrollStep = OkZoomerConfigManager.CONFIG.zoomValues.upperScrollSteps.value();
		int lowerScrollStep = OkZoomerConfigManager.CONFIG.zoomValues.lowerScrollSteps.value();

		zoomStep = increase ? Math.min(zoomStep + 1, upperScrollStep) :  Math.max(zoomStep - 1, -lowerScrollStep);

		validateZoomCulling(minecraft);

		if (zoomStep > 0) {
			Zoom.setZoomDivisor(zoomDivisor + ((maximumZoomDivisor - zoomDivisor) / upperScrollStep * zoomStep));
		} else if (zoomStep == 0) {
			Zoom.setZoomDivisor(zoomDivisor);
		} else {
			Zoom.setZoomDivisor(zoomDivisor + ((minimumZoomDivisor - zoomDivisor) / lowerScrollStep * -zoomStep));
		}
	}

	// The method used by both the "Reset Zoom" keybind and the "Reset Zoom With Mouse" tweak
	public static void resetZoomDivisor(boolean userPrompted) {
		if (!userPrompted && !OkZoomerConfigManager.CONFIG.tweaks.forgetZoomDivisor.value()) return;

		Zoom.resetZoomDivisor();
		zoomStep = 0;
		if (userPrompted) {
			validateZoomCulling();
		}
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
					client.getToastManager().addToast(SystemToast.multiline(
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
					client.getToastManager().addToast(SystemToast.multiline(
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
		return ModUtils.id(path);
	}

	public static boolean hasSpyglass(LocalPlayer player) {
		return hasSpyglass.test(player);
	}

	public static void addSpyglassProvider(Predicate<LocalPlayer> provider) {
		hasSpyglass = hasSpyglass.or(provider);
	}
}
