package io.github.ennuil.ok_zoomer.config;

public class ConfigEnums {
	public enum CinematicCameraOptions implements ConfigEnum {
		OFF,
		VANILLA,
		MULTIPLIED
	}

	public enum ZoomTransitionOptions implements ConfigEnum {
		OFF,
		SMOOTH,
		LINEAR
	}

	public enum ZoomModes implements ConfigEnum {
		HOLD,
		TOGGLE,
		PERSISTENT
	}

	public enum ZoomOverlays implements ConfigEnum {
		OFF,
		VIGNETTE,
		SPYGLASS
	}

	public enum SpyglassMode implements ConfigEnum {
		OFF,
		REQUIRE_ITEM,
		REPLACE_ZOOM,
		BOTH
	}

	public enum ZoomPresets implements ConfigEnum {
		CAMERA,
		COMPETITIVE,
		CLASSIC,
		PERSISTENT,
		SPYGLASS
	}

	public interface ConfigEnum {}
}
