package io.github.ennuil.ok_zoomer.config;

public class ConfigEnums {
	public enum CinematicCameraOptions {
		OFF,
		VANILLA,
		MULTIPLIED;
	}

	public enum ZoomTransitionOptions {
		OFF,
		SMOOTH,
		LINEAR;
	}

	public enum ZoomModes {
		HOLD,
		TOGGLE,
		PERSISTENT;
	}

	public enum ZoomOverlays {
		OFF,
		VIGNETTE,
		SPYGLASS;
	}

	public enum SpyglassDependency {
		OFF,
		REQUIRE_ITEM,
		REPLACE_ZOOM,
		BOTH;
	}
}
