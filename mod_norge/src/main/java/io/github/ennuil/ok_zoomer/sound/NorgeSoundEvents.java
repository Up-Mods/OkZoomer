package io.github.ennuil.ok_zoomer.sound;

import io.github.ennuil.ok_zoomer.utils.ModUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NorgeSoundEvents {
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ModUtils.MOD_NAMESPACE);

	public static final Holder<SoundEvent> ZOOM_IN = SOUND_EVENTS.register("zoom.zoom_in", SoundEvent::createVariableRangeEvent);
	public static final Holder<SoundEvent> ZOOM_OUT = SOUND_EVENTS.register("zoom.zoom_out", SoundEvent::createVariableRangeEvent);
	public static final Holder<SoundEvent> SCROLL = SOUND_EVENTS.register("zoom.scroll", SoundEvent::createVariableRangeEvent);

	public static void init() {}
}
