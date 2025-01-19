package io.github.ennuil.ok_zoomer.sound;

import io.github.ennuil.ok_zoomer.utils.ModUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class FabricSoundEvents {
	public static final SoundEvent ZOOM_IN = Registry.register(
		BuiltInRegistries.SOUND_EVENT,
		ModUtils.id("zoom.zoom_in"),
		SoundEvent.createVariableRangeEvent(ModUtils.id("zoom.zoom_in"))
	);

	public static final SoundEvent ZOOM_OUT = Registry.register(
		BuiltInRegistries.SOUND_EVENT,
		ModUtils.id("zoom.zoom_out"),
		SoundEvent.createVariableRangeEvent(ModUtils.id("zoom.zoom_out"))
	);

	public static void init() {}
}
