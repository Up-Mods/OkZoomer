package io.github.ennuil.ok_zoomer.packets;

import io.github.ennuil.ok_zoomer.config.ConfigEnums;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.CinematicCameraOptions;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.SpyglassMode;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.packets.payloads.*;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.qsl.networking.api.CustomPayloads;
import org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking;
import org.quiltmc.qsl.networking.api.client.ClientPlayConnectionEvents;

/* 	Manages the zoom packets and their signals.
	These packets are intended to be used by the future "Zoomer Boomer" server-side mod,
	although developers are welcome to independently transmit them for other loaders */
public class ZoomPackets {
	// The IDs for packets that allows the server to have some control on the zoom.
	public static final ResourceLocation DISABLE_ZOOM_PACKET_ID = ZoomUtils.id("disable_zoom");
	public static final ResourceLocation DISABLE_ZOOM_SCROLLING_PACKET_ID = ZoomUtils.id("disable_zoom_scrolling");
	public static final ResourceLocation FORCE_CLASSIC_MODE_PACKET_ID = ZoomUtils.id("force_classic_mode");
	public static final ResourceLocation FORCE_ZOOM_DIVISOR_PACKET_ID = ZoomUtils.id("force_zoom_divisor");
	public static final ResourceLocation ACKNOWLEDGE_MOD_PACKET_ID = ZoomUtils.id("acknowledge_mod");
	public static final ResourceLocation FORCE_SPYGLASS_PACKET_ID = ZoomUtils.id("force_spyglass");
	public static final ResourceLocation FORCE_SPYGLASS_OVERLAY_PACKET_ID = ZoomUtils.id("force_spyglass_overlay");

	public static void applyDisableZooming() {
		disableZoom = true;
	}

	public enum Acknowledgement {
		NONE,
		HAS_RESTRICTIONS,
		HAS_NO_RESTRICTIONS
	}

	// The signals used by other parts of the zoom in order to enforce the packets
	private static boolean hasRestrictions = false;
	private static boolean disableZoom = false;
	private static boolean disableZoomScrolling = false;
	private static boolean forceClassicMode = false;
	private static boolean forceZoomDivisors = false;
	private static Acknowledgement acknowledgement = Acknowledgement.NONE;
	private static double maximumZoomDivisor = 0.0D;
	private static double minimumZoomDivisor = 0.0D;
	private static boolean forceSpyglassMode = false;
	private static boolean forceSpyglassOverlay = false;

	private static final Component TOAST_TITLE = Component.translatable("toast.ok_zoomer.title");

	public static void sendToast(Minecraft client, Component description) {
		if (OkZoomerConfigManager.CONFIG.tweaks.showRestrictionToasts.value()) {
			client.getToasts().addToast(SystemToast.multiline(client, ZoomUtils.TOAST_ID, TOAST_TITLE, description));
		}
	}

	private static <T extends CustomPacketPayload> void registerConfigurationPacket(ResourceLocation id, FriendlyByteBuf.Reader<T> reader, ClientConfigurationNetworking.CustomChannelReceiver<T> handler) {
		CustomPayloads.registerS2CPayload(id, reader);
		ClientConfigurationNetworking.registerGlobalReceiver(id, handler);
	}

	//Registers all the packets
	public static void registerPackets() {
		/*  The "Disable Zoom" packet,
			If this packet is received, Ok Zoomer's zoom will be disabled completely while in the server
			Supported since Ok Zoomer 4.0.0 (1.16)
			Arguments: None */
		registerConfigurationPacket(DISABLE_ZOOM_PACKET_ID, DisableZoomPacket::fromPacket, DisableZoomPacket::handle);

		/*  The "Disable Zoom Scrolling" packet,
			If this packet is received, zoom scrolling will be disabled while in the server
			Supported since Ok Zoomer 4.0.0 (1.16)
			Arguments: None */
		registerConfigurationPacket(DISABLE_ZOOM_SCROLLING_PACKET_ID, DisableZoomScrollingPacket::fromPacket, DisableZoomScrollingPacket::handle);

		/*  The "Force Classic Mode" packet,
			If this packet is received, the Classic Mode will be activated while connected to the server,
			under the Classic mode, the Classic preset will be forced on all non-cosmetic options
			Supported since Ok Zoomer 5.0.0-beta.1 (1.17)
			Arguments: None */
		registerConfigurationPacket(FORCE_CLASSIC_MODE_PACKET_ID, ForceClassicModePacket::fromPacket, ForceClassicModePacket::handle);

		/*  The "Force Zoom Divisor" packet,
			If this packet is received, the minimum and maximum zoom divisor values will be overriden
			with the provided arguments
			Supported since Ok Zoomer 5.0.0-beta.2 (1.17)
			Arguments: One double (max & min) or two doubles (first is max, second is min) */
		registerConfigurationPacket(FORCE_ZOOM_DIVISOR_PACKET_ID, ForceZoomDivisorPacket::fromPacket, ForceZoomDivisorPacket::handle);

		/*  The "Acknowledge Mod" packet,
			If received, a toast will appear, the toast will either state that
			the server won't restrict the mod or say that the server controls will be activated
			Supported since Ok Zoomer 5.0.0-beta.2 (1.17)
			Arguments: one boolean, false for restricting, true for restrictionless */
		registerConfigurationPacket(ACKNOWLEDGE_MOD_PACKET_ID, AcknowledgeModPacket::fromPacket, AcknowledgeModPacket::handle);

		/*  The "Force Spyglass" packet,
			This packet lets the server to impose a spyglass restriction
			Supported since Ok Zoomer 5.0.0-beta.4 (1.18.2)
			Arguments: 2 booleans: requireItem and replaceZoom */
		registerConfigurationPacket(FORCE_SPYGLASS_PACKET_ID, ForceSpyglassPacket::fromPacket, ForceSpyglassPacket::handle);

		/*  The "Force Spyglass Overlay" packet,
			This packet will let the server restrict the mod to spyglass-only usage
			Not supported yet!
			Arguments: None */
		registerConfigurationPacket(FORCE_SPYGLASS_OVERLAY_PACKET_ID, ForceSpyglassOverlayPacket::fromPacket, ForceSpyglassOverlayPacket::handle);

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			if (ZoomPackets.hasRestrictions) {
				ZoomPackets.resetPacketSignals();
			}
		});
	}

	public static boolean hasRestrictions() {
		return hasRestrictions;
	}

	public static void checkRestrictions() {
		boolean hasRestrictions = disableZoom
			|| disableZoomScrolling
			|| forceClassicMode
			|| forceZoomDivisors
			|| forceSpyglassMode
			|| forceSpyglassOverlay;

		ZoomPackets.hasRestrictions = hasRestrictions;
		if (hasRestrictions) {
			ZoomPackets.acknowledgement = Acknowledgement.HAS_RESTRICTIONS;
		} else {
			ZoomPackets.acknowledgement = Acknowledgement.HAS_NO_RESTRICTIONS;
		}
	}

	public static boolean shouldDisableZoom() {
		return disableZoom;
	}

	public static boolean shouldDisableZoomScrolling() {
		return disableZoomScrolling;
	}

	public static boolean shouldForceClassicMode() {
		return forceClassicMode;
	}

	public static boolean shouldForceZoomDivisors() {
		return forceZoomDivisors;
	}

	public static Acknowledgement getAcknowledgement() {
		return acknowledgement;
	}

	public static double getMaximumZoomDivisor() {
		return maximumZoomDivisor;
	}

	public static double getMinimumZoomDivisor() {
		return minimumZoomDivisor;
	}

	public static boolean shouldForceSpyglassMode() {
		return forceSpyglassMode;
	}

	public static boolean shouldForceSpyglassOverlay() {
		return forceSpyglassOverlay;
	}

	public static void applyDisableZoomScrolling() {
		disableZoomScrolling = true;
		OkZoomerConfigManager.CONFIG.features.zoomScrolling.setOverride(false);
		OkZoomerConfigManager.CONFIG.features.extraKeyBinds.setOverride(false);
	}

	public static void applyClassicMode() {
		forceClassicMode = true;
		ZoomPackets.applyDisableZoomScrolling();
		OkZoomerConfigManager.CONFIG.features.cinematicCamera.setOverride(CinematicCameraOptions.VANILLA);
		OkZoomerConfigManager.CONFIG.features.reduceSensitivity.setOverride(false);
		OkZoomerConfigManager.CONFIG.zoomValues.zoomDivisor.setOverride(4.0D);
		OkZoomerConfigManager.configureZoomInstance();
	}

	public static void applyForcedZoomDivisor(double max, double min) {
		maximumZoomDivisor = max;
		minimumZoomDivisor = min;
		forceZoomDivisors = true;
	}

	public static void applySpyglassDependency(boolean requireItem, boolean replaceZoom) {
		OkZoomerConfigManager.CONFIG.features.spyglassMode.setOverride(requireItem
			? (replaceZoom ? SpyglassMode.BOTH : SpyglassMode.REQUIRE_ITEM)
			: (replaceZoom ? SpyglassMode.REPLACE_ZOOM : null));
		forceSpyglassMode = true;
	}

	public static void applyForceSpyglassOverlay() {
		OkZoomerConfigManager.CONFIG.features.zoomOverlay.setOverride(ConfigEnums.ZoomOverlays.SPYGLASS);
		forceSpyglassOverlay = true;
	}

	//The method used to reset the signals once left the server.
	private static void resetPacketSignals() {
		ZoomPackets.hasRestrictions = false;
		ZoomPackets.disableZoom = false;
		ZoomPackets.disableZoomScrolling = false;
		OkZoomerConfigManager.CONFIG.features.zoomScrolling.removeOverride();
		OkZoomerConfigManager.CONFIG.features.extraKeyBinds.removeOverride();
		ZoomPackets.forceClassicMode = false;
		OkZoomerConfigManager.CONFIG.features.cinematicCamera.removeOverride();
		OkZoomerConfigManager.CONFIG.features.reduceSensitivity.removeOverride();
		OkZoomerConfigManager.CONFIG.zoomValues.zoomDivisor.removeOverride();
		ZoomPackets.forceZoomDivisors = false;
		ZoomPackets.maximumZoomDivisor = 0.0D;
		ZoomPackets.minimumZoomDivisor = 0.0D;
		ZoomPackets.acknowledgement = Acknowledgement.NONE;
		ZoomPackets.forceSpyglassMode = false;
		OkZoomerConfigManager.CONFIG.features.spyglassMode.removeOverride();
		ZoomPackets.forceSpyglassOverlay = false;
		OkZoomerConfigManager.CONFIG.features.zoomScrolling.removeOverride();
	}
}
