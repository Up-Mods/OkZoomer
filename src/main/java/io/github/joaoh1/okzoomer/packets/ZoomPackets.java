package io.github.joaoh1.okzoomer.packets;

import io.github.joaoh1.okzoomer.config.OkZoomerConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

//Manages the zoom packets and their signals.
public class ZoomPackets {
    //The IDs for packets that allows the server to have some control on the zoom.
	public static final Identifier DISABLE_ZOOM_PACKET_ID = new Identifier("okzoomer", "disable_zoom");
	public static final Identifier DISABLE_ZOOM_SCROLLING_PACKET_ID = new Identifier("okzoomer", "disable_zoom_scrolling");
	public static final Identifier FORCE_CLASSIC_MODE_PACKET_ID = new Identifier("okzoomer", "force_classic_mode");
	//TODO - Force Zoom Divisor (allows for either a single divisor or a maximum and minimum)
	public static final Identifier FORCE_ZOOM_DIVISOR_PACKET_ID = new Identifier("okzoomer", "force_zoom_divisor");
	//TODO - Reset (allows to reset the other restrictions, allowing Zoomer Boomer to change restrictions without kicking the user)

    //The signals used by other parts of the zoom in order to enforce the packets. 
	private static boolean disableZoom = false;
	private static boolean disableZoomScrolling = false;
	private static boolean forceClassicMode = false;
	private static boolean forceZoomDivisors = false;
	public static double maximumZoomDivisor = 0.0D;
	public static double minimumZoomDivisor = 0.0D;

	private static TranslatableText toastTitle = new TranslatableText("toast.okzoomer.title");

	private static void sendToast(MinecraftClient client, TranslatableText description) {
		SystemToast.add(client.getToastManager(), SystemToast.Type.TUTORIAL_HINT, toastTitle, description);
	}

	//Registers all the packets
    public static void registerPackets() {
		ClientPlayNetworking.registerGlobalReceiver(DISABLE_ZOOM_PACKET_ID, (client, handler, buf, responseSender) -> {
			client.execute(() -> {
				sendToast(client, new TranslatableText("toast.okzoomer.disable_zoom"));
				disableZoom = true;
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(DISABLE_ZOOM_SCROLLING_PACKET_ID, (client, handler, buf, responseSender) -> {
			client.execute(() -> {
				sendToast(client, new TranslatableText("toast.okzoomer.disable_zoom_scrolling"));
				disableZoomScrolling = true;
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(FORCE_CLASSIC_MODE_PACKET_ID, (client, handler, buf, responseSender) -> {
			client.execute(() -> {
				sendToast(client, new TranslatableText("toast.okzoomer.force_classic_mode"));
				disableZoomScrolling = true;
				forceClassicMode = true;
				OkZoomerConfig.configureZoomInstance();
			});
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			PacketByteBuf emptyBuf = PacketByteBufs.empty();
			//sender.sendPacket(DISABLE_ZOOM_PACKET_ID, emptyBuf);
			//sender.sendPacket(DISABLE_ZOOM_SCROLLING_PACKET_ID, emptyBuf);
			sender.sendPacket(FORCE_CLASSIC_MODE_PACKET_ID, emptyBuf);
			//PacketByteBuf buf = PacketByteBufs.create();
			//buf.writeInt(1);
			//sender.sendPacket(FORCE_REDUCE_SENSITIVITY_PACKET_ID, buf);
		}); 

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			if (ZoomPackets.disableZoom || ZoomPackets.disableZoomScrolling || ZoomPackets.forceClassicMode) {
				ZoomPackets.resetPacketSignals();	
			}
		});
	}

	public static boolean getDisableZoom() {
		return disableZoom;
	}
	
	public static boolean getDisableZoomScrolling() {
		return disableZoomScrolling;
	}

	public static boolean getForceClassicMode() {
		return forceClassicMode;
	}

	public static boolean getForceZoomDivisors() {
		return forceZoomDivisors;
	}
	
	//The method used to reset the signals once left the server.
	private static void resetPacketSignals() {
		ZoomPackets.disableZoom = false;
		ZoomPackets.disableZoomScrolling = false;
		if (ZoomPackets.forceClassicMode) {
			ZoomPackets.forceClassicMode = false;
			OkZoomerConfig.configureZoomInstance();
		}
	}
}