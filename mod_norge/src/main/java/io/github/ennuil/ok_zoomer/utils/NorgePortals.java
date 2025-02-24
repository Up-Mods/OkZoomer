package io.github.ennuil.ok_zoomer.utils;

import io.github.ennuil.ok_zoomer.sound.NorgeSoundEvents;
import net.minecraft.sounds.SoundEvent;

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
