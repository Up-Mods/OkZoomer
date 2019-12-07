package io.github.joaoh1.okzoomer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class OkZoomer implements ClientModInitializer {
	private static FabricKeyBinding zoomKeyBinding = FabricKeyBinding.Builder.create(
			new Identifier("okzoomer", "zoom"),
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_Z,
			"key.categories.misc"
		).build();

	Boolean isCinematicModeOn = false;
	Boolean isZoomPressed = false;
	Double realFov = 70.0;
	Integer cinematicModeToggleCooldown = 1;

	@Override
	public void onInitializeClient() {
		KeyBindingRegistry.INSTANCE.register(zoomKeyBinding);

		ClientTickCallback.EVENT.register(e -> {
			
			if (MinecraftClient.getInstance().options.keySmoothCamera.isPressed()) {
				cinematicModeToggleCooldown -= 1;
				if (cinematicModeToggleCooldown <= 0) {
					cinematicModeToggleCooldown = 5;
					switch (isCinematicModeOn.toString()) {
						case "true":
							isCinematicModeOn = false;
							break;
						case "false":
							isCinematicModeOn = true;
							break;
					}
				}
			} else {
				cinematicModeToggleCooldown = 1;
			}

    		if (zoomKeyBinding.isPressed() || isCinematicModeOn == true) {
				MinecraftClient.getInstance().options.smoothCameraEnabled = true;
			} else {
				MinecraftClient.getInstance().options.smoothCameraEnabled = false;
			}

			if (zoomKeyBinding.isPressed()) {
				MinecraftClient.getInstance().options.fov = realFov * 0.5;
				isZoomPressed = true;
			} else {
				if (isZoomPressed) {
					MinecraftClient.getInstance().options.fov = realFov;
					isZoomPressed = false;
				}
				realFov = MinecraftClient.getInstance().options.fov;
			}
		});
	}
}
