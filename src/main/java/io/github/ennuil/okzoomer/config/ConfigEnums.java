package io.github.ennuil.okzoomer.config;

import net.minecraft.util.StringIdentifiable;

public class ConfigEnums {
    public enum CinematicCameraOptions implements StringIdentifiable {
        OFF,
        VANILLA,
        MULTIPLIED;

        @Override
        public String asString() {
            return this.toString();
        }
    }

    public enum ZoomTransitionOptions implements StringIdentifiable {
        OFF,
        SMOOTH,
        LINEAR;

        @Override
        public String asString() {
            return this.toString();
        }
    }

    public enum ZoomModes implements StringIdentifiable {
        HOLD,
        TOGGLE,
        PERSISTENT;

        @Override
        public String asString() {
            return this.toString();
        }
    }

    public enum ZoomOverlays implements StringIdentifiable {
        OFF,
        VIGNETTE,
        SPYGLASS;

        @Override
        public String asString() {
            return this.toString();
        }
    }

    public enum SpyglassDependency implements StringIdentifiable {
        OFF,
        REQUIRE_ITEM,
        REPLACE_ZOOM,
        BOTH;

        @Override
        public String asString() {
            return this.toString();
        }
    }
}
