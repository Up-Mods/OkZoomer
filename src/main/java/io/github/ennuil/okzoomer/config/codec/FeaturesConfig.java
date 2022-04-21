package io.github.ennuil.okzoomer.config.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.ennuil.okzoomer.config.ConfigEnums.CinematicCameraOptions;
import io.github.ennuil.okzoomer.config.ConfigEnums.SpyglassDependency;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomOverlays;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomTransitionOptions;
import net.minecraft.util.StringIdentifiable;

public class FeaturesConfig {
	public static final Codec<FeaturesConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			StringIdentifiable.createCodec(
				CinematicCameraOptions::values,
				CinematicCameraOptions::valueOf
			).fieldOf("cinematic_camera").orElse(CinematicCameraOptions.OFF).forGetter(FeaturesConfig::getCinematicCamera),
			Codec.BOOL.fieldOf("reduce_sensitivity").orElse(true).forGetter(FeaturesConfig::getReduceSensitivity),
			StringIdentifiable.createCodec(
				ZoomTransitionOptions::values,
				ZoomTransitionOptions::valueOf
			).fieldOf("zoom_transition").orElse(ZoomTransitionOptions.SMOOTH).forGetter(FeaturesConfig::getZoomTransition),
			StringIdentifiable.createCodec(
				ZoomModes::values,
				ZoomModes::valueOf
			).fieldOf("zoom_mode").orElse(ZoomModes.HOLD).forGetter(FeaturesConfig::getZoomMode),
			Codec.BOOL.fieldOf("zoom_scrolling").orElse(true).forGetter(FeaturesConfig::getZoomScrolling),
			Codec.BOOL.fieldOf("extra_keybinds").orElse(true).forGetter(FeaturesConfig::getExtraKeyBinds),
			StringIdentifiable.createCodec(
				ZoomOverlays::values,
				ZoomOverlays::valueOf
			).fieldOf("zoom_overlay").orElse(ZoomOverlays.OFF).forGetter(FeaturesConfig::getZoomOverlay),
			StringIdentifiable.createCodec(
				SpyglassDependency::values,
				SpyglassDependency::valueOf
			).fieldOf("spyglass_dependency").orElse(SpyglassDependency.OFF).forGetter(FeaturesConfig::getSpyglassDependency)
		)
		.apply(instance, FeaturesConfig::new)
	);

	private CinematicCameraOptions cinematicCamera;
	private boolean reduceSensitivity;
	private ZoomTransitionOptions zoomTransition;
	private ZoomModes zoomMode;
	private boolean zoomScrolling;
	private boolean extraKeyBinds;
	private ZoomOverlays zoomOverlay;
	private SpyglassDependency spyglassDependency;

	public FeaturesConfig(
		CinematicCameraOptions cinematicCamera,
		boolean reduceSensitivity,
		ZoomTransitionOptions zoomTransition,
		ZoomModes zoomMode,
		boolean zoomScrolling,
		boolean extraKeyBinds,
		ZoomOverlays zoomOverlay,
		SpyglassDependency spyglassDependency
	) {
		this.cinematicCamera = cinematicCamera;
		this.reduceSensitivity = reduceSensitivity;
		this.zoomTransition = zoomTransition;
		this.zoomMode = zoomMode;
		this.zoomScrolling = zoomScrolling;
		this.extraKeyBinds = extraKeyBinds;
		this.zoomOverlay = zoomOverlay;
		this.spyglassDependency = spyglassDependency;
	}

	public FeaturesConfig() {
		this.cinematicCamera = CinematicCameraOptions.OFF;
		this.reduceSensitivity = true;
		this.zoomTransition = ZoomTransitionOptions.SMOOTH;
		this.zoomMode = ZoomModes.HOLD;
		this.zoomScrolling = true;
		this.extraKeyBinds = true;
		this.zoomOverlay = ZoomOverlays.OFF;
		this.spyglassDependency = SpyglassDependency.OFF;
	}

	public CinematicCameraOptions getCinematicCamera() {
		return this.cinematicCamera;
	}

	public void setCinematicCamera(CinematicCameraOptions cinematicCamera) {
		this.cinematicCamera = cinematicCamera;
	}

	public boolean getReduceSensitivity() {
		return this.reduceSensitivity;
	}

	public void setReduceSensitivity(boolean reduceSensitivity) {
		this.reduceSensitivity = reduceSensitivity;
	}

	public ZoomTransitionOptions getZoomTransition() {
		return this.zoomTransition;
	}

	public void setZoomTransition(ZoomTransitionOptions zoomTransition) {
		this.zoomTransition = zoomTransition;
	}

	public ZoomModes getZoomMode() {
		return this.zoomMode;
	}

	public void setZoomMode(ZoomModes zoomMode) {
		this.zoomMode = zoomMode;
	}

	public boolean getZoomScrolling() {
		return this.zoomScrolling;
	}

	public void setZoomScrolling(boolean zoomScrolling) {
		this.zoomScrolling = zoomScrolling;
	}

	public boolean getExtraKeyBinds() {
		return this.extraKeyBinds;
	}

	public void setExtraKeyBinds(boolean extraKeyBinds) {
		this.extraKeyBinds = extraKeyBinds;
	}

	public ZoomOverlays getZoomOverlay() {
		return this.zoomOverlay;
	}

	public void setZoomOverlay(ZoomOverlays zoomOverlay) {
		this.zoomOverlay = zoomOverlay;
	}

	public SpyglassDependency getSpyglassDependency() {
		return spyglassDependency;
	}

	public void setSpyglassDependency(SpyglassDependency spyglassDependency) {
		this.spyglassDependency = spyglassDependency;
	}
}
