package io.github.ennuil.okzoomer;

import io.github.ennuil.okzoomer.events.LoadConfigEvent;
import io.github.ennuil.okzoomer.events.ManageExtraKeysEvent;
import io.github.ennuil.okzoomer.events.ManageZoomEvent;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import net.fabricmc.api.ClientModInitializer;

// This class is responsible for registering the events and packets
public class OkZoomerClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Load the events.
        LoadConfigEvent.registerEvent();
        ManageZoomEvent.registerEvent();
        ManageExtraKeysEvent.registerEvent();
        
        // Register the zoom-controlling packets.
        ZoomPackets.registerPackets();
    }
}
