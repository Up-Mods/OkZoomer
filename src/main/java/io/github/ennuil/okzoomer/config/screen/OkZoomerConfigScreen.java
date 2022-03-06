package io.github.ennuil.okzoomer.config.screen;

import java.util.Optional;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.option.SpruceBooleanOption;
import dev.lambdaurora.spruceui.option.SpruceCyclingOption;
import dev.lambdaurora.spruceui.option.SpruceSeparatorOption;
import dev.lambdaurora.spruceui.option.SpruceSimpleActionOption;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import io.github.ennuil.okzoomer.config.OkZoomerConfigManager;
import io.github.ennuil.okzoomer.config.ConfigEnums.CinematicCameraOptions;
import io.github.ennuil.okzoomer.config.ConfigEnums.SpyglassDependency;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomOverlays;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomTransitionOptions;
import io.github.ennuil.okzoomer.config.OkZoomerConfigManager.ZoomPresets;
import io.github.ennuil.okzoomer.config.codec.FeaturesConfig;
import io.github.ennuil.okzoomer.config.codec.OkZoomerConfig;
import io.github.ennuil.okzoomer.config.codec.TweaksConfig;
import io.github.ennuil.okzoomer.config.codec.ValuesConfig;
import io.github.ennuil.okzoomer.utils.ZoomUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class OkZoomerConfigScreen extends SpruceScreen {
    private final Screen parent;
    private SpruceOptionListWidget list;
    private ZoomPresets preset;
    private CustomTextureBackground normalBackground = new CustomTextureBackground(new Identifier("minecraft:textures/block/yellow_concrete.png"), 64, 64, 64, 255);
    private CustomTextureBackground darkenedBackground = new CustomTextureBackground(new Identifier("minecraft:textures/block/yellow_concrete.png"), 32, 32, 32, 255);

    private FeaturesConfig featuresConfig;
    private ValuesConfig valuesConfig;
    private TweaksConfig tweaksConfig;

    public OkZoomerConfigScreen(Screen parent) {
        super(new TranslatableText("config.okzoomer.title"));
        this.parent = parent;
        this.preset = ZoomPresets.DEFAULT;
    }

    // Unlike other options, the cycling option doesn't attach the prefix on the text;
    // So we do it ourselves automatically!
    private Text getCyclingOptionText(String text, Text prefix) {
        return new TranslatableText(
            "spruceui.options.generic",
            prefix,
            text != null ? new TranslatableText(text) : new LiteralText("Error"));
    }

    private void resetVariables() {
        this.featuresConfig = OkZoomerConfigManager.configInstance.features();
        this.valuesConfig = OkZoomerConfigManager.configInstance.values();
        this.tweaksConfig = OkZoomerConfigManager.configInstance.tweaks();
    }
    
    @Override
    protected void init() {
        super.init();
        this.list = new SpruceOptionListWidget(Position.of(0, 22), this.width, this.height - 36 - 22);
        this.list.setBackground(darkenedBackground);
        this.resetVariables();

        // "Features" category separator
        var featuresSeparator = new SpruceSeparatorOption(
            "config.okzoomer.category.features",
            true,
            new TranslatableText("config.okzoomer.category.features.tooltip"));

        // Cinematic Camera
        var cinematicCameraOption = new SpruceCyclingOption(
            "config.okzoomer.cinematic_camera",
            amount -> featuresConfig.setCinematicCamera(
                switch (featuresConfig.getCinematicCamera()) {
                    case OFF -> CinematicCameraOptions.VANILLA;
                    case VANILLA -> CinematicCameraOptions.MULTIPLIED;
                    case MULTIPLIED -> CinematicCameraOptions.OFF;
                    default -> CinematicCameraOptions.OFF;
                }
            ),
            option -> switch (featuresConfig.getCinematicCamera()) {
                case OFF -> getCyclingOptionText("config.okzoomer.cinematic_camera.off", option.getPrefix());
                case VANILLA -> getCyclingOptionText("config.okzoomer.cinematic_camera.vanilla", option.getPrefix());
                case MULTIPLIED -> getCyclingOptionText("config.okzoomer.cinematic_camera.multiplied", option.getPrefix());
                default -> getCyclingOptionText(null, option.getPrefix());
            },
            new TranslatableText("config.okzoomer.cinematic_camera.tooltip"));

        // Reduce Sensitivity
        var reduceSensitivityOption = new SpruceBooleanOption(
            "config.okzoomer.reduce_sensitivity",
            () -> featuresConfig.getReduceSensitivity(),
            value -> featuresConfig.setReduceSensitivity(value),
            new TranslatableText("config.okzoomer.reduce_sensitivity.tooltip"));

        // Zoom Transition
        var zoomTransitionOption = new SpruceCyclingOption(
            "config.okzoomer.zoom_transition",
            amount -> featuresConfig.setZoomTransition(
                switch (featuresConfig.getZoomTransition()) {
                    case OFF -> ZoomTransitionOptions.SMOOTH;
                    case SMOOTH -> ZoomTransitionOptions.LINEAR;
                    case LINEAR -> ZoomTransitionOptions.OFF;
                    default -> ZoomTransitionOptions.OFF;
                }
            ),
            option -> switch (featuresConfig.getZoomTransition()) {
                case OFF -> getCyclingOptionText("config.okzoomer.zoom_transition.off", option.getPrefix());
                case SMOOTH -> getCyclingOptionText("config.okzoomer.zoom_transition.smooth", option.getPrefix());
                case LINEAR -> getCyclingOptionText("config.okzoomer.zoom_transition.linear", option.getPrefix());
                default -> getCyclingOptionText(null, option.getPrefix());
            },
            new TranslatableText("config.okzoomer.zoom_transition.tooltip"));
        
        // Zoom Mode
        var zoomModeOption = new SpruceCyclingOption(
            "config.okzoomer.zoom_mode",
            amount -> featuresConfig.setZoomMode(switch (featuresConfig.getZoomMode()) {
                case HOLD -> ZoomModes.TOGGLE;
                case TOGGLE -> ZoomModes.PERSISTENT;
                case PERSISTENT -> ZoomModes.HOLD;
                default -> ZoomModes.HOLD;
            }),
            option -> switch (featuresConfig.getZoomMode()) {
                case HOLD -> getCyclingOptionText("options.key.hold", option.getPrefix());
                case TOGGLE -> getCyclingOptionText("options.key.toggle", option.getPrefix());
                case PERSISTENT -> getCyclingOptionText("config.okzoomer.zoom_mode.persistent", option.getPrefix());
                default -> getCyclingOptionText(null, option.getPrefix());
            },
            new TranslatableText("config.okzoomer.zoom_mode.tooltip"));
        
        // Zoom Scrolling
        var zoomScrollingOption = new SpruceBooleanOption(
            "config.okzoomer.zoom_scrolling",
            () -> featuresConfig.getZoomScrolling(),
            value -> featuresConfig.setZoomScrolling(value),
            new TranslatableText("config.okzoomer.zoom_scrolling.tooltip"));
        
        // Extra Key Binds
        var extraKeyBindsOption = new SpruceBooleanOption(
            "config.okzoomer.extra_key_binds",
            () -> featuresConfig.getExtraKeyBinds(),
            value -> featuresConfig.setExtraKeyBinds(value),
            new TranslatableText("config.okzoomer.extra_key_binds.tooltip"));
        
        // Zoom Overlay
        var zoomOverlayOption = new SpruceCyclingOption(
            "config.okzoomer.zoom_overlay",
            amount -> featuresConfig.setZoomOverlay(
                switch (featuresConfig.getZoomOverlay()) {
                    case OFF -> ZoomOverlays.VIGNETTE;
                    case VIGNETTE -> ZoomOverlays.SPYGLASS;
                    case SPYGLASS -> ZoomOverlays.OFF;
                    default -> ZoomOverlays.OFF;
                }
            ),
            option -> switch (featuresConfig.getZoomOverlay()) {
                case OFF -> getCyclingOptionText("config.okzoomer.zoom_overlay.off", option.getPrefix());
                case VIGNETTE -> getCyclingOptionText("config.okzoomer.zoom_overlay.vignette", option.getPrefix());
                case SPYGLASS -> getCyclingOptionText("config.okzoomer.zoom_overlay.spyglass", option.getPrefix());
                default -> getCyclingOptionText(null, option.getPrefix());
            },
            new TranslatableText("config.okzoomer.zoom_overlay.tooltip"));
        
        // Zoom Overlay
        var spyglassDependencyOption = new SpruceCyclingOption(
            "config.okzoomer.spyglass_dependency",
            amount -> featuresConfig.setSpyglassDependency(
                switch (featuresConfig.getSpyglassDependency()) {
                    case OFF -> SpyglassDependency.REQUIRE_ITEM;
                    case REQUIRE_ITEM -> SpyglassDependency.REPLACE_ZOOM;
                    case REPLACE_ZOOM -> SpyglassDependency.BOTH;
                    case BOTH -> SpyglassDependency.OFF;
                    default -> SpyglassDependency.OFF;
                }
            ),
            option -> switch (featuresConfig.getSpyglassDependency()) {
                case OFF -> getCyclingOptionText("config.okzoomer.spyglass_dependency.off", option.getPrefix());
                case REQUIRE_ITEM -> getCyclingOptionText("config.okzoomer.spyglass_dependency.require_item", option.getPrefix());
                case REPLACE_ZOOM -> getCyclingOptionText("config.okzoomer.spyglass_dependency.replace_zoom", option.getPrefix());
                case BOTH -> getCyclingOptionText("config.okzoomer.spyglass_dependency.both", option.getPrefix());
                default -> getCyclingOptionText(null, option.getPrefix());
            },
            new TranslatableText("config.okzoomer.spyglass_dependency.tooltip"));
        
        // "Values" category separator
        var valuesSeparator = new SpruceSeparatorOption(
            "config.okzoomer.category.values",
            true,
            new TranslatableText("config.okzoomer.category.values.tooltip"));

        // Zoom Divisor
        var zoomDivisorOption = new SpruceBoundedDoubleInputOption(
            "config.okzoomer.zoom_divisor",
            4.0D, Optional.of(Double.MIN_NORMAL), Optional.empty(),
            () -> valuesConfig.getZoomDivisor(),
            value -> valuesConfig.setZoomDivisor(value),
            new TranslatableText("config.okzoomer.zoom_divisor.tooltip"));
        
        // Minimum Zoom Divisor
        var minimumZoomDivisorOption = new SpruceBoundedDoubleInputOption(
            "config.okzoomer.minimum_zoom_divisor",
            1.0D, Optional.of(Double.MIN_NORMAL), Optional.empty(),
            () -> valuesConfig.getMinimumZoomDivisor(),
            value -> valuesConfig.setMinimumZoomDivisor(value),
            new TranslatableText("config.okzoomer.minimum_zoom_divisor.tooltip"));

        // Maximum Zoom Divisor
        var maximumZoomDivisorOption = new SpruceBoundedDoubleInputOption(
            "config.okzoomer.maximum_zoom_divisor",
            50.0D, Optional.of(Double.MIN_NORMAL), Optional.empty(),
            () -> valuesConfig.getMaximumZoomDivisor(),
            value -> valuesConfig.setMaximumZoomDivisor(value),
            new TranslatableText("config.okzoomer.maximum_zoom_divisor.tooltip"));
        
        // Upper Scroll Step
        var scrollStepOption = new SpruceBoundedIntegerInputOption(
            "config.okzoomer.upper_scroll_steps",
            20, Optional.of(0), Optional.empty(),
            () -> valuesConfig.getUpperScrollSteps(),
            value -> valuesConfig.setUpperScrollStep(value),
            new TranslatableText("config.okzoomer.upper_scroll_steps.tooltip"));
        
        // Lower Scroll Step        
        var lowerScrollStepOption = new SpruceBoundedIntegerInputOption(
            "config.okzoomer.lower_scroll_steps",
            4, Optional.of(0), Optional.empty(),
            () -> valuesConfig.getLowerScrollSteps(),
            value -> valuesConfig.setLowerScrollStep(value),
            new TranslatableText("config.okzoomer.lower_scroll_steps.tooltip"));
        
        // Smooth Multiplier
        var smoothMultiplierOption = new SpruceBoundedDoubleInputOption(
            "config.okzoomer.smooth_multiplier",
            0.75D, Optional.of(Double.MIN_NORMAL), Optional.of(1.0D),
            () -> valuesConfig.getSmoothMultiplier(),
            value -> valuesConfig.setSmoothMultiplier(value),
            new TranslatableText("config.okzoomer.smooth_multiplier.tooltip"));
        
        // Cinematic Multiplier
        var cinematicMultiplierOption = new SpruceBoundedDoubleInputOption(
            "config.okzoomer.cinematic_multiplier",
            4.0D, Optional.of(Double.MIN_NORMAL), Optional.empty(),
            () -> valuesConfig.getCinematicMultiplier(),
            value -> valuesConfig.setCinematicMultiplier(value),
            new TranslatableText("config.okzoomer.cinematic_multiplier.tooltip"));
        
        // Minimum Linear Step
        var minimumLinearStepOption = new SpruceBoundedDoubleInputOption(
            "config.okzoomer.minimum_linear_step",
            0.125D, Optional.of(0.0D), Optional.empty(),
            () -> valuesConfig.getMinimumLinearStep(),
            value -> valuesConfig.setMinimumLinearStep(value),
            new TranslatableText("config.okzoomer.minimum_linear_step.tooltip"));
        
        // Maximum Linear Step
        var maximumLinearStepOption = new SpruceBoundedDoubleInputOption(
            "config.okzoomer.maximum_linear_step",
            0.25D, Optional.of(0.25D), Optional.empty(),
            () -> valuesConfig.getMaximumLinearStep(),
            value -> valuesConfig.setMaximumLinearStep(value),
            new TranslatableText("config.okzoomer.maximum_linear_step.tooltip"));
        
        // "Tweaks" category separator
        var tweaksSeparator = new SpruceSeparatorOption(
            "config.okzoomer.category.tweaks",
            true,
            new TranslatableText("config.okzoomer.category.tweaks.tooltip"));

        // Reset Zoom with Mouse
        var resetZoomWithMouseOption = new SpruceBooleanOption(
            "config.okzoomer.reset_zoom_with_mouse",
            () -> tweaksConfig.getResetZoomWithMouse(),
            value -> tweaksConfig.setResetZoomWithMouse(value),
            new TranslatableText("config.okzoomer.reset_zoom_with_mouse.tooltip"));
        
        // Unbind Conflicting Key
        var unbindConflictingKeyOption = SpruceSimpleActionOption.of(
            "config.okzoomer.unbind_conflicting_key",
            button -> ZoomUtils.unbindConflictingKey(client, true),
            new TranslatableText("config.okzoomer.unbind_conflicting_key.tooltip"));
        
        // Use Spyglass Texture
        var useSpyglassTextureOption = new SpruceBooleanOption(
            "config.okzoomer.use_spyglass_texture",
            () -> tweaksConfig.getUseSpyglassTexture(),
            value -> tweaksConfig.setUseSpyglassTexture(value),
            new TranslatableText("config.okzoomer.use_spyglass_texture.tooltip"));
        
        // Use Spyglass Sounds
        var useSpyglassSoundsOption = new SpruceBooleanOption(
            "config.okzoomer.use_spyglass_sounds",
            () -> tweaksConfig.getUseSpyglassSounds(),
            value -> tweaksConfig.setUseSpyglassSounds(value),
            new TranslatableText("config.okzoomer.use_spyglass_sounds.tooltip"));
        
        // Show Restriction Toasts
        var showRestrictionToastsOption = new SpruceBooleanOption(
            "config.okzoomer.show_restriction_toasts",
            () -> tweaksConfig.getShowRestrictionToasts(),
            value -> tweaksConfig.setShowRestrictionToasts(value),
            new TranslatableText("config.okzoomer.show_restriction_toasts.tooltip"));
        
        // Print owo on Start
        var printOwoOnStartOption = new SpruceBooleanOption(
            "config.okzoomer.print_owo_on_start",
            () -> tweaksConfig.getPrintOwoOnStart(),
            value -> tweaksConfig.setPrintOwoOnStart(value),
            new TranslatableText("config.okzoomer.print_owo_on_start.tooltip"));
        
        // "Reset" category separator
        var resetSeparator = new SpruceSeparatorOption(
            "config.okzoomer.category.reset",
            true,
            new TranslatableText("config.okzoomer.category.reset.tooltip"));

        // Preset
        var presetOption = new SpruceCyclingOption(
            "config.okzoomer.preset",
            amount -> this.preset = switch (this.preset) {
                case DEFAULT -> ZoomPresets.CLASSIC;
                case CLASSIC -> ZoomPresets.PERSISTENT;
                case PERSISTENT -> ZoomPresets.SPYGLASS;
                case SPYGLASS -> ZoomPresets.DEFAULT;
                default -> ZoomPresets.DEFAULT;
            },
            option -> switch (this.preset) {
                case DEFAULT -> getCyclingOptionText("config.okzoomer.preset.default", option.getPrefix());
                case CLASSIC -> getCyclingOptionText("config.okzoomer.preset.classic", option.getPrefix());
                case PERSISTENT -> getCyclingOptionText("config.okzoomer.preset.persistent", option.getPrefix());
                case SPYGLASS -> getCyclingOptionText("config.okzoomer.preset.spyglass", option.getPrefix());
                default -> getCyclingOptionText(null, option.getPrefix());
            },
            new TranslatableText("config.okzoomer.preset.tooltip"));
        
        // Reset Settings
        var resetSettingsOption = SpruceSimpleActionOption.of(
            "config.okzoomer.reset_settings",
            button -> {
                OkZoomerConfigManager.resetToPreset(this.preset);
                double scrollAmount = this.list.getScrollAmount();
                this.init(client, width, height);
                this.list.setScrollAmount(scrollAmount);
            },
            new TranslatableText("config.okzoomer.reset_settings.tooltip"));
        
        
        this.list.addSingleOptionEntry(featuresSeparator);
        this.list.addOptionEntry(cinematicCameraOption, reduceSensitivityOption);
        this.list.addSingleOptionEntry(zoomTransitionOption);
        this.list.addOptionEntry(zoomModeOption, zoomScrollingOption);
        this.list.addOptionEntry(extraKeyBindsOption, zoomOverlayOption);
        this.list.addSingleOptionEntry(spyglassDependencyOption);

        this.list.addSingleOptionEntry(valuesSeparator);
        this.list.addSingleOptionEntry(zoomDivisorOption);
        this.list.addOptionEntry(minimumZoomDivisorOption, maximumZoomDivisorOption);
        this.list.addOptionEntry(scrollStepOption, lowerScrollStepOption);
        this.list.addOptionEntry(smoothMultiplierOption, cinematicMultiplierOption);
        this.list.addOptionEntry(minimumLinearStepOption, maximumLinearStepOption);

        this.list.addSingleOptionEntry(tweaksSeparator);
        this.list.addOptionEntry(resetZoomWithMouseOption, unbindConflictingKeyOption);
        this.list.addOptionEntry(useSpyglassTextureOption, useSpyglassSoundsOption);
        this.list.addOptionEntry(showRestrictionToastsOption, printOwoOnStartOption);

        this.list.addSingleOptionEntry(resetSeparator);
        this.list.addOptionEntry(presetOption, resetSettingsOption);

        this.addDrawableChild(this.list);
        this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 - 154, this.height - 28), 150, 20, new TranslatableText("config.okzoomer.discard_changes"),
            btn -> {
                this.resetVariables();
                OkZoomerConfigManager.loadModConfig();
                double scrollAmount = this.list.getScrollAmount();
                this.init(client, width, height);
                this.list.setScrollAmount(scrollAmount);
            }).asVanilla());
        this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 + 4, this.height - 28), 150, 20, SpruceTexts.GUI_DONE,
            btn -> {
                OkZoomerConfigManager.configInstance = new OkZoomerConfig(
                    this.featuresConfig,
                    this.valuesConfig,
                    this.tweaksConfig
                );
                this.client.setScreen(this.parent);
            }).asVanilla());
    }

    @Override
    public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
    }

    @Override
    public void renderBackground(MatrixStack matrices, int vOffset) {
        normalBackground.render(matrices, this, vOffset);
    }

    @Override
	public void removed() {
		OkZoomerConfigManager.saveModConfig();
	}

    @Override
    public void onClose() {
        this.client.setScreen(this.parent);
    }
}
