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

	public enum ScrollingModes implements ConfigEnum {
		EXPONENTIAL,
		LEGACY
	}

	public enum ZoomOverlays implements ConfigEnum {
		OFF,
		VIGNETTE,
		SPYGLASS
	}

	public enum SpyglassModes implements ConfigEnum {
		OFF,
		REQUIRE_ITEM,
		REPLACE_ZOOM,
		BOTH
	}

	public enum SeeDistantEntitiesModes implements ConfigEnum {
		OFF,
		SAFE,
		ON
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
