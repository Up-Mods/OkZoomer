package io.github.joaoh1.okzoomer.client.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

//Manages the zoom packets and their signals.
public class ZoomPackets {
    //The IDs for packets that allows the server to have some control on the zoom.
	public static final Identifier DISABLE_ZOOM_PACKET_ID = new Identifier("okzoomer", "disable_zoom");
    public static final Identifier DISABLE_ZOOM_SCROLLING_PACKET_ID = new Identifier("okzoomer", "disable_zoom_scrolling");

    //The signals used by other parts of the zoom in order to enforce the packets. 
	public static boolean disableZoom = false;
	public static boolean disableZoomScrolling = false;
	
	//Registers all the packets
    public static void registerPackets() {
		ClientPlayNetworking.registerGlobalReceiver(DISABLE_ZOOM_PACKET_ID, (client, handler, buf, responseSender) -> {
			client.execute(() -> {
				client.getToastManager().add(
					SystemToast.create(
						client, SystemToast.Type.TUTORIAL_HINT,
						new TranslatableText("toast.okzoomer.title"),
						new TranslatableText("toast.okzoomer.disable_zoom")
					)
				);
				disableZoom = true;
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(DISABLE_ZOOM_SCROLLING_PACKET_ID, (client, handler, buf, responseSender) -> {
			client.execute(() -> {
				client.getToastManager().add(
					SystemToast.create(
						client, SystemToast.Type.TUTORIAL_HINT,
						new TranslatableText("toast.okzoomer.title"),
						new TranslatableText("toast.okzoomer.disable_zoom_scrolling")
					)
				);
				disableZoomScrolling = true;
			});
		});
	}
	
	//The method used to reset the signals once left the server.
	public static void resetPacketSignals() {
		ZoomPackets.disableZoom = false;
		ZoomPackets.disableZoomScrolling = false;
	}
}