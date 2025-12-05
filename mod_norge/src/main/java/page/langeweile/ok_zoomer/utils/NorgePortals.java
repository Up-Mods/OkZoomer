package page.langeweile.ok_zoomer.utils;

import net.minecraft.sounds.SoundEvent;
import page.langeweile.ok_zoomer.sound.NorgeSoundEvents;

public class NorgePortals {
	public static SoundEvent getZoomInSound() {
		return NorgeSoundEvents.ZOOM_IN.value();
	}

	public static SoundEvent getZoomOutSound() {
		return NorgeSoundEvents.ZOOM_OUT.value();
	}

	public static SoundEvent getScrollSound() {
		return NorgeSoundEvents.SCROLL.value();
	}
}
