package io.github.ennuil.ok_zoomer.utils;

import io.github.ennuil.ok_zoomer.sound.ForgeSoundEvents;
import net.minecraft.sounds.SoundEvent;

public class ForgePortals {
	public static SoundEvent getZoomInSound() {
		return ForgeSoundEvents.ZOOM_IN.get();
	}

	public static SoundEvent getZoomOutSound() {
		return ForgeSoundEvents.ZOOM_OUT.get();
	}
}
