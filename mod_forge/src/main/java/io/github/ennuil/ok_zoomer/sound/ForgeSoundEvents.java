package io.github.ennuil.ok_zoomer.sound;

import io.github.ennuil.ok_zoomer.utils.ModUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ForgeSoundEvents {
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, ModUtils.MOD_NAMESPACE);

	public static final RegistryObject<SoundEvent> ZOOM_IN = SOUND_EVENTS.register("zoom.zoom_in", () -> SoundEvent.createVariableRangeEvent(ModUtils.id("zoom.zoom_in")));
	public static final RegistryObject<SoundEvent> ZOOM_OUT = SOUND_EVENTS.register("zoom.zoom_out", () -> SoundEvent.createVariableRangeEvent(ModUtils.id("zoom.zoom_out")));
	public static final RegistryObject<SoundEvent> SCROLL = SOUND_EVENTS.register("zoom.scroll", () -> SoundEvent.createVariableRangeEvent(ModUtils.id("zoom.scroll")));

	public static void init() {}
}
