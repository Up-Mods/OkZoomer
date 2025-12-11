package page.langeweile.ok_zoomer.utils;

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
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.zoom.Zoom;

import java.util.function.Predicate;

// The class that contains most of the logic behind the zoom itself
public class ZoomUtils {
	// The logger, used everywhere to print messages to the console
	public static final Logger LOGGER = LoggerFactory.getLogger("Ok Zoomer");

	public static final SystemToast.SystemToastId TOAST_ID = new SystemToast.SystemToastId();

	public static final TagKey<Item> ZOOM_DEPENDENCIES_TAG = TagKey.create(Registries.ITEM, ModUtils.id("zoom_dependencies"));

	public static int zoomStep = 0;

	private static Predicate<LocalPlayer> hasSpyglass = player -> player.isCreative();
	private static boolean safeSmartOcclusion = false;
	private static Float fadeModifier = null;

	// The method used for changing the zoom divisor, used by zoom scrolling and the key binds
	public static void changeZoomDivisor(boolean increase) {
		// TODO - Replace client references with an argument
		var minecraft = Minecraft.getInstance();
		int scrollBase = OkZoomerConfigManager.CONFIG.zoomValues.scrollBase.value();
		int scrollResolution = OkZoomerConfigManager.CONFIG.zoomValues.scrollResolution.value();
		int upperScrollStep = OkZoomerConfigManager.CONFIG.zoomValues.scrollStepLimit.value();
		int lowerScrollStep = 0;

		int lastZoomStep = zoomStep;
		zoomStep = increase ? Math.min(zoomStep + 1, upperScrollStep) :  Math.max(zoomStep - 1, -lowerScrollStep);

		double divisor = 1.0;
		if (zoomStep != 0) {
			divisor = Math.pow(scrollBase, (double) zoomStep / scrollResolution);
			Zoom.setZoomDivisor(divisor);
		} else {
			Zoom.setZoomDivisor(1);
		}

		if (lastZoomStep != zoomStep && OkZoomerConfigManager.CONFIG.tweaks.scrollSounds.value()) {
			minecraft.player.playSound(Portals.getScrollSound(), 1.0F, 1.0F);
		}

		if (OkZoomerConfigManager.CONFIG.tweaks.debugScrolling.value()) {
			minecraft.player.displayClientMessage(Component.literal( zoomStep + " - " + divisor), true);
		}
	}

	// The method used by both the "Reset Zoom" keybind and the "Reset Zoom With Mouse" tweak
	public static void resetZoomDivisor(boolean userPrompted) {
		if (!userPrompted && !OkZoomerConfigManager.CONFIG.tweaks.forgetZoomDivisor.value()) return;

		int scrollBase = OkZoomerConfigManager.CONFIG.zoomValues.scrollBase.value();
		int scrollResolution = OkZoomerConfigManager.CONFIG.zoomValues.scrollResolution.value();
		zoomStep = OkZoomerConfigManager.CONFIG.zoomValues.defaultScrollStep.value();
		Zoom.setZoomDivisor(Math.pow(scrollBase, (double) zoomStep / scrollResolution));
	}

	public static void keepZoomStepsWithinBounds() {
		zoomStep = Mth.clamp(zoomStep, 0, OkZoomerConfigManager.CONFIG.zoomValues.scrollStepLimit.value());
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

	public static Float getFadeModifier() {
		return ZoomUtils.fadeModifier;
	}

	public static void setFadeModifier(Float fadeModifier) {
		ZoomUtils.fadeModifier = fadeModifier;
	}
}
