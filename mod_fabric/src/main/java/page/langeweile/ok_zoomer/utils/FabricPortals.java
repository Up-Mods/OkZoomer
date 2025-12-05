package page.langeweile.ok_zoomer.utils;

import net.minecraft.sounds.SoundEvent;
import page.langeweile.ok_zoomer.sound.FabricSoundEvents;

public class FabricPortals {
	public static SoundEvent getZoomInSound() {
		return FabricSoundEvents.ZOOM_IN;
	}

	public static SoundEvent getZoomOutSound() {
		return FabricSoundEvents.ZOOM_OUT;
	}

	public static SoundEvent getScrollSound() {
		return FabricSoundEvents.SCROLL;
	}
}
