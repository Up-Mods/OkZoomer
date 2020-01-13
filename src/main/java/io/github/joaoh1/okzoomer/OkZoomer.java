package io.github.joaoh1.okzoomer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import org.lwjgl.glfw.GLFW;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;

public class OkZoomer implements ClientModInitializer {
  private static MinecraftClient minecraft = MinecraftClient.getInstance();

  public static final FabricKeyBinding zoomKeyBinding = FabricKeyBinding.Builder
      .create(new Identifier("okzoomer", "zoom"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "key.categories.misc")
      .build();

  public boolean toggleBooleanByKeybind(boolean toggledBoolean, int cooldown) {
    cooldown -= 1;
    if (cooldown <= 1) {
      cooldown = 3;
      toggledBoolean = !toggledBoolean;
    }

    return toggledBoolean;
  }

  private static boolean hideHandsBecauseZoom = false;

  public static boolean shouldHideHands() {
    boolean hideHands = hideHandsBecauseZoom;
    return hideHands;
  }

  boolean cinematicMode = false;
  boolean fovProcessing = true;
  boolean zoomPressed = false;

  double realFov = 70.0;
  double smoothing = 1.0 - 0.5;

  int cinematicModeToggleCooldown = 1;
  int zoomToggleCooldown = 1;
  int timesToRepeatZoomCheck = 1;
  int zoomProgress = 0;

  @Override
  public void onInitializeClient() {
    AutoConfig.register(OkZoomerConfig.class, Toml4jConfigSerializer::new);
		OkZoomerConfig config = AutoConfig.getConfigHolder(OkZoomerConfig.class).getConfig();
		
		KeyBindingRegistry.INSTANCE.register(zoomKeyBinding);

		ClientTickCallback.EVENT.register(e -> {
      //If Zoom Toggle is enabled, Minecraft is paused and zoom's toggled in, toggle out.
			if (config.zoomToggle) {
        if (minecraft.isPaused() && zoomPressed) {
          zoomPressed = false;
          zoomKeyBinding.setPressed(true);
        }
      }

      //If Smooth Camera is enabled, reimplement the Smooth Camera function.
			if (config.smoothCamera) {
				if (minecraft.options.keySmoothCamera.isPressed()) {
					cinematicMode = toggleBooleanByKeybind(cinematicMode, cinematicModeToggleCooldown);
					cinematicModeToggleCooldown = 3;
				} else {
					cinematicModeToggleCooldown = 1;
				}
        
        //But also consider the zoom's smooth camera.
				if (zoomProgress == 2 || cinematicMode) {
          minecraft.options.smoothCameraEnabled = true;
				} else {
					minecraft.options.smoothCameraEnabled = false;
				}
      }
      
      //If Smooth Transitions are enabled, repeat the code more than once to guarantee smoothiness.
      if (config.smoothTransition) {
        timesToRepeatZoomCheck = config.advancedSmoothTransSettings.timesToRepeatSmoothing;
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
            smoothing *= config.advancedSmoothTransSettings.transitionStartMultiplier;
            zoomProgress = 1;
            if (!config.smoothTransition || config.zoomMultiplier == 1.0) {
              smoothing = config.zoomMultiplier;
            }
            if (smoothing >= config.zoomMultiplier) {
              smoothing = config.zoomMultiplier / config.advancedSmoothTransSettings.smoothDivisor;
              minecraft.options.fov = realFov * config.zoomMultiplier;
              if (config.hideHands) {
                hideHandsBecauseZoom = true;
                minecraft.gameRenderer.tick();
              }
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
            smoothing *= config.advancedSmoothTransSettings.transitionEndMultiplier;
              zoomProgress = 1;
              if (!config.smoothTransition || config.zoomMultiplier == 1.0) {
                smoothing = config.zoomMultiplier;
              }
              if (smoothing >= config.zoomMultiplier) {
                smoothing = config.zoomMultiplier / config.advancedSmoothTransSettings.smoothDivisor;
                minecraft.options.fov = realFov;
                if (hideHandsBecauseZoom) {
                  hideHandsBecauseZoom = false;
                  minecraft.gameRenderer.tick();
                }
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
            smoothing *= config.advancedSmoothTransSettings.transitionEndMultiplier;
            zoomProgress = 0;
            if (!config.smoothTransition || config.zoomMultiplier == 1.0) {
              smoothing = config.zoomMultiplier;
            }
            if (smoothing >= config.zoomMultiplier) {
              smoothing = config.zoomMultiplier / config.advancedSmoothTransSettings.smoothDivisor;
              minecraft.options.fov = realFov;
              if (hideHandsBecauseZoom) {
                hideHandsBecauseZoom = false;
                minecraft.gameRenderer.tick();
              }
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
            smoothing = config.zoomMultiplier / config.advancedSmoothTransSettings.smoothDivisor;
            realFov = minecraft.options.fov;
          }
        }   
      }
		});
	}
}
