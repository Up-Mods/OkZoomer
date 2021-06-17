package io.github.ennuil.okzoomer.config;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.option.SpruceBooleanOption;
import dev.lambdaurora.spruceui.option.SpruceCyclingOption;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.CinematicCameraOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

// TODO - Rewrite the config screen, again².
public class OkZoomerSpruceConfigScreen extends SpruceScreen {
    private final Screen parent;
    private SpruceOptionListWidget list;

    public OkZoomerSpruceConfigScreen(Screen parent) {
        super(new TranslatableText("config.okzoomer.title"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        super.init();
        this.list = new SpruceOptionListWidget(Position.of(0, 22), this.width, this.height - 22);
        
        // Cinematic Camera
        var cinematicCameraOption = new SpruceCyclingOption(
            "config.okzoomer.cinematic_camera",
            amount -> OkZoomerConfigPojo.features.cinematicCamera = switch (OkZoomerConfigPojo.features.cinematicCamera) {
                case OFF -> CinematicCameraOptions.VANILLA;
                case VANILLA -> CinematicCameraOptions.MULTIPLIED;
                case MULTIPLIED -> CinematicCameraOptions.OFF;
                default -> CinematicCameraOptions.OFF;
            },
            option -> switch (OkZoomerConfigPojo.features.cinematicCamera) {
                case OFF -> new TranslatableText("config.okzoomer.cinematic_camera.off");
                case VANILLA -> new TranslatableText("config.okzoomer.cinematic_camera.vanilla");
                case MULTIPLIED -> new TranslatableText("config.okzoomer.cinematic_camera.multiplied");
                default -> new LiteralText("Error");
            },
            new TranslatableText("config.okzoomer.cinematic_camera.tooltip"));

        // Reduce Sensitivity
        var reduceSensitivityOption = new SpruceBooleanOption(
            "config.okzoomer.reduce_sensitivity",
            () -> OkZoomerConfigPojo.features.reduceSensitivity,
            value -> OkZoomerConfigPojo.features.reduceSensitivity = value,
            new TranslatableText("config.okzoomer.reduce_sensitivity.tooltip"));

        
        this.list.addOptionEntry(cinematicCameraOption, reduceSensitivityOption);
        this.addDrawableChild(this.list);
    }

    @Override
    public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
    }

    @Override
	public void removed() {
		OkZoomerConfig.configureZoomInstance();
	}

    @Override
    public void onClose() {
        this.client.openScreen(this.parent);
    }
}
