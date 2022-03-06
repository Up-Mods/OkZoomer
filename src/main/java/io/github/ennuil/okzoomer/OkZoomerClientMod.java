package io.github.ennuil.okzoomer;

import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import io.github.ennuil.okzoomer.packets.ZoomPackets;
import net.fabricmc.loader.api.ModContainer;

// This class is responsible for registering the packets
public class OkZoomerClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer container) {
        // Register the zoom-controlling packets.
        ZoomPackets.registerPackets();
    }
}
