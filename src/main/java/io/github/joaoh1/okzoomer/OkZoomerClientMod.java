package io.github.joaoh1.okzoomer;

import io.github.joaoh1.okzoomer.events.LoadConfigEvent;
import io.github.joaoh1.okzoomer.events.ManageExtraKeysEvent;
import io.github.joaoh1.okzoomer.events.ManageZoomEvent;
import io.github.joaoh1.okzoomer.packets.ZoomPackets;
import net.fabricmc.api.ClientModInitializer;

//This class is responsible for registering the events and packets.
public class OkZoomerClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		//Load the events.
		LoadConfigEvent.registerEvent();
		ManageZoomEvent.registerEvent();
		ManageExtraKeysEvent.registerEvent();
		
		//Register the zoom-controlling packets.
		ZoomPackets.registerPackets();
	}
}
