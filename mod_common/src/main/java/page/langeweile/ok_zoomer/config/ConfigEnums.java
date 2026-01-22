package page.langeweile.ok_zoomer.config;

public class ConfigEnums {
	public enum ZoomTransitionModes implements ConfigEnum {
		INSTANT,
		LINEAR,
		SMOOTH,
		SINE,
		BALANCED,
		SPRING
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
		ZOOMINATOR,
		PERSISTENT,
		SPYGLASS
	}

	public interface ConfigEnum {}
}
