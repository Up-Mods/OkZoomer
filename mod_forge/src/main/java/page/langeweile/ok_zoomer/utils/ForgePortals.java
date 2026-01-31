package page.langeweile.ok_zoomer.utils;

import net.minecraft.sounds.SoundEvent;
import page.langeweile.ok_zoomer.sound.ForgeSoundEvents;

public class ForgePortals {
	public static SoundEvent getZoomInSound() {
		return ForgeSoundEvents.ZOOM_IN.get();
	}

	public static SoundEvent getZoomOutSound() {
		return ForgeSoundEvents.ZOOM_OUT.get();
	}

	public static SoundEvent getScrollSound() {
		return ForgeSoundEvents.SCROLL.get();
	}
}
