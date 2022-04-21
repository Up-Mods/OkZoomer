package io.github.ennuil.okzoomer.config.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TweaksConfig {
	public static final Codec<TweaksConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			Codec.BOOL.fieldOf("reset_zoom_with_mouse").orElse(true).forGetter(TweaksConfig::getResetZoomWithMouse),
			Codec.BOOL.fieldOf("unbind_conflicting_key").orElse(true).forGetter(TweaksConfig::getUnbindConflictingKey),
			Codec.BOOL.fieldOf("use_spyglass_texture").orElse(false).forGetter(TweaksConfig::getUseSpyglassTexture),
			Codec.BOOL.fieldOf("use_spyglass_sounds").orElse(false).forGetter(TweaksConfig::getUseSpyglassSounds),
			Codec.BOOL.fieldOf("show_restriction_toasts").orElse(true).forGetter(TweaksConfig::getShowRestrictionToasts),
			Codec.BOOL.fieldOf("print_owo_on_start").orElse(true).forGetter(TweaksConfig::getPrintOwoOnStart)
		)
		.apply(instance, TweaksConfig::new)
	);

	private boolean resetZoomWithMouse;
	private boolean unbindConflictingKey;
	private boolean useSpyglassTexture;
	private boolean useSpyglassSounds;
	private boolean showRestrictionToasts;
	private boolean printOwoOnStart;

	public TweaksConfig(
		boolean resetZoomWithMouse,
		boolean unbindConflictingKey,
		boolean useSpyglassTexture,
		boolean useSpyglassSounds,
		boolean showRestrictionToasts,
		boolean printOwoOnStart
	) {
		this.resetZoomWithMouse = resetZoomWithMouse;
		this.unbindConflictingKey = unbindConflictingKey;
		this.useSpyglassTexture = useSpyglassTexture;
		this.useSpyglassSounds = useSpyglassSounds;
		this.showRestrictionToasts = showRestrictionToasts;
		this.printOwoOnStart = printOwoOnStart;
	}

	public TweaksConfig() {
		this.resetZoomWithMouse = true;
		this.unbindConflictingKey = true;
		this.useSpyglassTexture = false;
		this.useSpyglassSounds = false;
		this.showRestrictionToasts = true;
		this.printOwoOnStart = true;
	}

	public boolean getResetZoomWithMouse() {
		return this.resetZoomWithMouse;
	}

	public void setResetZoomWithMouse(boolean resetZoomWithMouse) {
		this.resetZoomWithMouse = resetZoomWithMouse;
	}

	public boolean getUnbindConflictingKey() {
		return this.unbindConflictingKey;
	}

	public void setUnbindConflictingKey(boolean unbindConflictingKey) {
		this.unbindConflictingKey = unbindConflictingKey;
	}

	public boolean getUseSpyglassTexture() {
		return this.useSpyglassTexture;
	}

	public void setUseSpyglassTexture(boolean useSpyglassTexture) {
		this.useSpyglassTexture = useSpyglassTexture;
	}

	public boolean getUseSpyglassSounds() {
		return this.useSpyglassSounds;
	}

	public void setUseSpyglassSounds(boolean useSpyglassSounds) {
		this.useSpyglassSounds = useSpyglassSounds;
	}

	public boolean getShowRestrictionToasts() {
		return this.showRestrictionToasts;
	}

	public void setShowRestrictionToasts(boolean showRestrictionToasts) {
		this.showRestrictionToasts = showRestrictionToasts;
	}

	public boolean getPrintOwoOnStart() {
		return this.printOwoOnStart;
	}

	public void setPrintOwoOnStart(boolean printOwoOnStart) {
		this.printOwoOnStart = printOwoOnStart;
	}
}
