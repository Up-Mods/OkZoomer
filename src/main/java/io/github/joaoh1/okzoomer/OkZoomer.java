package io.github.joaoh1.okzoomer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.lwjgl.glfw.GLFW;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;

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
  
  public static double round(double number, int places) {
		BigDecimal bd = BigDecimal.valueOf(number);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	Boolean cinematicMode = false;
  Boolean fovProcessing = true;
  Boolean zoomPressed = false;

  Double realFov = 70.0;
	Double smoothing = 1.0 - 0.5;

  Integer cinematicModeToggleCooldown = 1;
  Integer timesToRepeatZoomCheck = 1;
  Integer zoomProgress = 0;
	Integer zoomToggleCooldown = 1;

	@Override
	public void onInitializeClient() {
		AutoConfig.register(OkZoomerConfig.class, JanksonConfigSerializer::new);
		OkZoomerConfig config = AutoConfig.getConfigHolder(OkZoomerConfig.class).getConfig();
		
		KeyBindingRegistry.INSTANCE.register(zoomKeyBinding);

		ClientTickCallback.EVENT.register(e -> {
			if (config.zoomToggle) {
        if (minecraft.isPaused() && zoomPressed) {
          zoomPressed = false;
          zoomKeyBinding.setPressed(true);
        }
      }

			if (config.smoothCamera) {
				if (minecraft.options.keySmoothCamera.isPressed()) {
					cinematicMode = toggleBooleanByKeybind(cinematicMode, cinematicModeToggleCooldown);
					cinematicModeToggleCooldown = 3;
				} else {
					cinematicModeToggleCooldown = 1;
				}
	
				if (zoomProgress == 2 || cinematicMode) {
					minecraft.options.smoothCameraEnabled = true;
				} else {
					minecraft.options.smoothCameraEnabled = false;
				}
      }
      
      if (config.smoothTransition) {
        timesToRepeatZoomCheck = 4;
      } else {
        timesToRepeatZoomCheck = 1;
      }

      for (int i = 0; i < timesToRepeatZoomCheck; i++) {
        if (zoomKeyBinding.isPressed() || zoomProgress == 1) {
          if (config.zoomToggle) {
            zoomPressed = toggleBooleanByKeybind(zoomPressed, zoomToggleCooldown);
            zoomToggleCooldown = 3;
          } else {
            zoomPressed = true;
          }
  
          if (zoomPressed && zoomProgress != 2) {
            smoothing = round(smoothing * 2, 4);
            zoomProgress = 1;
            if (!config.smoothTransition || config.zoomMultiplier == 1.0) {
              smoothing = config.zoomMultiplier;
            }
            if (smoothing >= config.zoomMultiplier) {
              smoothing = config.zoomMultiplier / config.smoothDivisor;
              minecraft.options.fov = realFov * config.zoomMultiplier;
              fovProcessing = false;
              zoomProgress = 2;
            } else {
              if (config.zoomMultiplier > 1.0) {
                minecraft.options.fov = realFov * (1.0 + smoothing);
              } else {
                minecraft.options.fov = realFov * (1.0 - smoothing);
              }
            }
          } else if ((!zoomPressed && zoomProgress == 2)|| zoomProgress == 1) {
              smoothing = round(smoothing * 2, 4);
              zoomProgress = 1;
              if (!config.smoothTransition || config.zoomMultiplier == 1.0) {
                smoothing = config.zoomMultiplier;
              }
              if (smoothing >= config.zoomMultiplier) {
                smoothing = config.zoomMultiplier / config.smoothDivisor;
                minecraft.options.fov = realFov;
                fovProcessing = true;
                zoomProgress = 0;
              } else {
                if (config.zoomMultiplier > 1.0) {
                  minecraft.options.fov = realFov * (config.zoomMultiplier - smoothing);
                } else {
                  minecraft.options.fov = realFov * (config.zoomMultiplier + smoothing);
                }
              }
          }
        } else {
          if (config.zoomToggle) {
            zoomToggleCooldown = 1;
          } else if (zoomProgress != 0 || !fovProcessing || zoomProgress == 1) {
            smoothing = round(smoothing * 2, 4);
            zoomProgress = 0;
            if (!config.smoothTransition || config.zoomMultiplier == 1.0) {
              smoothing = config.zoomMultiplier;
            }
            if (smoothing >= config.zoomMultiplier) {
              smoothing = config.zoomMultiplier / config.smoothDivisor;
              minecraft.options.fov = realFov;
              fovProcessing = true;
              zoomProgress = 0;
              zoomPressed = false;
            } else if (config.zoomMultiplier > 1.0) {
              minecraft.options.fov = realFov * (config.zoomMultiplier - smoothing);
            } else {
              minecraft.options.fov = realFov * (config.zoomMultiplier + smoothing);
            }
          } 
          if (fovProcessing && zoomProgress == 0) {
            smoothing = config.zoomMultiplier / config.smoothDivisor;
            realFov = minecraft.options.fov;
          }
        }   
      }
		});
	}
}
