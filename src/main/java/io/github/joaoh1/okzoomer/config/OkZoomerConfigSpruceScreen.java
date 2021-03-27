package io.github.joaoh1.okzoomer.config;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.option.SpruceCyclingOption;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import io.github.joaoh1.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.CinematicCameraOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

// TODO - Rewrite the config screen, again².
public class OkZoomerConfigSpruceScreen extends SpruceScreen {
    private final Screen parent;
    private SpruceOptionListWidget list;

    private CinematicCameraOptions cinematicCameraOption = OkZoomerConfigPojo.features.cinematicCamera;

    public OkZoomerConfigSpruceScreen(Screen parent) {
        super(new TranslatableText("config.okzoomer.title"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        super.init();
        this.list = new SpruceOptionListWidget(Position.of(0, 0), this.width, this.height);
        this.list.addOptionEntry(
            new SpruceCyclingOption(
                "config.okzoomer.cinematic_camera",
                (value) -> {
                    switch (cinematicCameraOption) {
                        case OFF:
                            cinematicCameraOption = CinematicCameraOptions.VANILLA;
                            break;
                        case VANILLA:
                            cinematicCameraOption = CinematicCameraOptions.MULTIPLIED;
                            break;
                        case MULTIPLIED:
                            cinematicCameraOption = CinematicCameraOptions.OFF;
                        default:
                            break;
                    }
                    OkZoomerConfigPojo.features.cinematicCamera = cinematicCameraOption;
                },
                (option) -> {
                    switch (cinematicCameraOption) {
                        case OFF:
                            return new TranslatableText("config.okzoomer.cinematic_camera.off");
                        case VANILLA:
                            return new TranslatableText("config.okzoomer.cinematic_camera.vanilla");
                        case MULTIPLIED:
                            return new TranslatableText("config.okzoomer.cinematic_camera.multiplied");
                        default:
                            return new LiteralText("Error");
                    }
                },
                new TranslatableText("config.okzoomer.cinematic_camera.tooltip")), null);
        this.addChild(this.list);
    }
}
