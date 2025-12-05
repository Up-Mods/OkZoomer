package page.langeweile.ok_zoomer.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import page.langeweile.ok_zoomer.utils.ModUtils;

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

	public static final SoundEvent SCROLL = Registry.register(
		BuiltInRegistries.SOUND_EVENT,
		ModUtils.id("zoom.scroll"),
		SoundEvent.createVariableRangeEvent(ModUtils.id("zoom.scroll"))
	);

	public static void init() {}
}
