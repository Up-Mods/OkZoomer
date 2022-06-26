package io.github.ennuil.ok_zoomer.config;

public class ConfigEnums {
	public enum CinematicCameraOptions implements ConfigEnum {
		OFF,
		VANILLA,
		MULTIPLIED;

		@Override
		public Enum<?> next() {
			var enumValues = this.getDeclaringClass().getEnumConstants();
			return enumValues[this.ordinal() + 1 < enumValues.length ? this.ordinal() + 1 : 0];
		}
	}

	public enum ZoomTransitionOptions implements ConfigEnum {
		OFF,
		SMOOTH,
		LINEAR;

		@Override
		public Enum<?> next() {
			var enumValues = this.getDeclaringClass().getEnumConstants();
			return enumValues[this.ordinal() + 1 < enumValues.length ? this.ordinal() + 1 : 0];
		}
	}

	public enum ZoomModes implements ConfigEnum {
		HOLD,
		TOGGLE,
		PERSISTENT;

		@Override
		public Enum<?> next() {
			var enumValues = this.getDeclaringClass().getEnumConstants();
			return enumValues[this.ordinal() + 1 < enumValues.length ? this.ordinal() + 1 : 0];
		}
	}

	public enum ZoomOverlays implements ConfigEnum {
		OFF,
		VIGNETTE,
		SPYGLASS;

		@Override
		public Enum<?> next() {
			var enumValues = this.getDeclaringClass().getEnumConstants();
			return enumValues[this.ordinal() + 1 < enumValues.length ? this.ordinal() + 1 : 0];
		}
	}

	public enum SpyglassDependency implements ConfigEnum {
		OFF,
		REQUIRE_ITEM,
		REPLACE_ZOOM,
		BOTH;

		@Override
		public Enum<?> next() {
			var enumValues = this.getDeclaringClass().getEnumConstants();
			return enumValues[this.ordinal() + 1 < enumValues.length ? this.ordinal() + 1 : 0];
		}
	}

	public enum ZoomPresets implements ConfigEnum {
		DEFAULT,
		CLASSIC,
		PERSISTENT,
		SPYGLASS;

		@Override
		public Enum<?> next() {
			var enumValues = this.getDeclaringClass().getEnumConstants();
			return enumValues[this.ordinal() + 1 < enumValues.length ? this.ordinal() + 1 : 0];
		}
	}

	public interface ConfigEnum {
		Enum<?> next();
	}
}
