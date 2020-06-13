package io.github.joaoh1.okzoomer.main;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class OkZoomerMod implements ModInitializer {
	public static final Identifier DISABLE_REDUCE_SENSITIVITY_PACKET_ID = new Identifier("okzoomer", "disable_reduce_sensitivity");
	public static final Identifier DISABLE_ZOOMING_PACKET_ID = new Identifier("okzoomer", "disable_zooming");
	public static final Identifier DISABLE_ZOOM_SCROLLING_PACKET_ID = new Identifier("okzoomer", "disable_zoom_scrolling");
	public static final Identifier FORCE_OPTIFINE_MODE_PACKET_ID = new Identifier("okzoomer", "force_optifine_mode");

	@Override
	public void onInitialize() {}   
}