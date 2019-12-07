package io.github.joaoh1.okzoomer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class OkZoomer implements ClientModInitializer {
	private static MinecraftClient minecraft = MinecraftClient.getInstance();

	private static FabricKeyBinding zoomKeyBinding = FabricKeyBinding.Builder.create(
			new Identifier("okzoomer", "zoom"),
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_Z,
			"key.categories.misc"
		).build();

	private Boolean toggleBooleanByKeybind(Boolean toggledBoolean, Integer cooldown) {
		cooldown -= 1;
		if (cooldown <= 1) {
			cooldown = 3;
			toggledBoolean = !toggledBoolean;
		}

		return toggledBoolean;
	}
	
	Boolean smoothCamera = true;
	Boolean smoothTransition = false;
	Boolean zoomToggle = false;

	Boolean cinematicMode = false;
	Boolean fovProcessing = true;
	Boolean zoomPressed = false;

	Double realFov = 70.0;
	Double smoothing = 0.5;

	Integer cinematicModeToggleCooldown = 1;
	Integer zoomToggleCooldown = 1;

	@Override
	public void onInitializeClient() {
		KeyBindingRegistry.INSTANCE.register(zoomKeyBinding);

		ClientTickCallback.EVENT.register(e -> {
			if (minecraft.isPaused() && zoomPressed) {
				zoomPressed = false;
				zoomKeyBinding.setPressed(true);
			}

			if (smoothCamera == true) {
				if (minecraft.options.keySmoothCamera.isPressed()) {
					cinematicMode = toggleBooleanByKeybind(cinematicMode, cinematicModeToggleCooldown);
					cinematicModeToggleCooldown = 3;
				} else {
					cinematicModeToggleCooldown = 1;
				}
	
				if (zoomPressed == true || cinematicMode == true) {
					minecraft.options.smoothCameraEnabled = true;
				} else {
					minecraft.options.smoothCameraEnabled = false;
				}
			}

			if (zoomKeyBinding.isPressed()) {
				if (zoomToggle) {
					zoomPressed = toggleBooleanByKeybind(zoomPressed, zoomToggleCooldown);
					zoomToggleCooldown = 3;
				} else {
					zoomPressed = true;
				}

				if (zoomPressed == true) {
					if (smoothTransition == false) {
						smoothing = 0.0;
					}
					if (smoothing > 0.05) {
						smoothing *= 0.25;
						minecraft.options.fov = realFov * (0.5 + smoothing);
					} else if (smoothing < 0.05) {
						smoothing = 0.05;
						minecraft.options.fov = realFov * 0.5;
						fovProcessing = false;
					}
				} else if (zoomPressed == false) {
					smoothing /= 0.2;
					if (smoothTransition == false) {
						smoothing = 0.5;
					}
					minecraft.options.fov = realFov * (0.5 + smoothing);
					if (smoothing >= 0.5) {
						smoothing = 0.5;
						minecraft.options.fov = realFov;
						fovProcessing = true;
					}
				}
			} else {
				if (zoomToggle) {
					zoomToggleCooldown = 1;
				} else {
					if (!fovProcessing) {
						smoothing /= 0.2;
						if (smoothTransition == false) {
							smoothing = 0.5;
						}
						minecraft.options.fov = realFov * (0.5 + smoothing);
						if (smoothing >= 0.5) {
							smoothing = 0.5;
							minecraft.options.fov = realFov;
							fovProcessing = true;
							zoomPressed = false;
						}
					}
				}

				if (fovProcessing && smoothing == 0.5) {
					smoothing = 0.5;
					realFov = minecraft.options.fov;
				}
			}
		});
	}
}
