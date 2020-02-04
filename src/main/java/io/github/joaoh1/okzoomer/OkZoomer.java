package io.github.joaoh1.okzoomer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import org.lwjgl.glfw.GLFW;

public class OkZoomer implements ClientModInitializer {
  private static final MinecraftClient minecraft = MinecraftClient.getInstance();

	//The keybind itself
  public static final FabricKeyBinding zoomKeyBinding = FabricKeyBinding.Builder
  	.create(new Identifier("okzoomer", "zoom"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "key.categories.misc")
    .build();

	//Toggles a boolean if the matching cooldown is over.
  public static boolean toggleBooleanByKeybind(boolean toggledBoolean, int cooldown) {
    cooldown -= 1;
    if (cooldown <= 1) {
      cooldown = 3;
      toggledBoolean = !toggledBoolean;
    }
    return toggledBoolean;
  }

  //Used by the mixin that hides the hands.
  private static boolean hideHandsBecauseZoom = false;
  public static boolean shouldHideHands() {
    return hideHandsBecauseZoom;
  }

  //Used by the mixin that handles the smooth transition.
  private static boolean shouldZoomSmoothly = false;
  public static boolean shouldZoomSmoothly() {
    return shouldZoomSmoothly;
  }

  //Used by the mixin that overrides the scrolling.
  private static boolean shouldScrollZoom = false;
  public static boolean shouldScrollZoom() {
    return shouldScrollZoom;
  }

  double zoomDivisor = 4.0;

  //Increases or decreases the zoom, used by zoom scrolling.
  public static double scrollZoom(double providedZoomDivisor, double scrollAmount, double minimumValue, double maximumValue) {
    double generalAmount = 0.0;
    if (providedZoomDivisor <= 5.0) {
      generalAmount = 0.25;
    }

    if (providedZoomDivisor > 5.0 && providedZoomDivisor <= 8.0) {
      generalAmount = 0.5;
    }

    if (providedZoomDivisor > 8.0) {
      generalAmount = 1.0;
    }

    if (providedZoomDivisor > 20.0) {
      generalAmount = 2.0;
    }

    if (scrollAmount > 0.0D) {
      scrollAmount = generalAmount;
    } else if (scrollAmount < 0.0D) {
      scrollAmount = -generalAmount;
    }

    if (shouldScrollZoom) {
      providedZoomDivisor += scrollAmount;

      if (providedZoomDivisor <= minimumValue) {
        return minimumValue;
      }

      if (providedZoomDivisor >= maximumValue) {
        return maximumValue;
      }

      return providedZoomDivisor;
    } else {
      return providedZoomDivisor;
    }
  }

	//Internal booleans, one for mimicking Cinematic Mode, another replaces zoomKeybinding.isPressed().
  boolean cinematicMode = false;
  boolean zoomPressed = false;
	
  double realFov = 70.0;
  double realSensitivity = 100.0;
  double smoothing = 1.0 - 0.5;
  double sensitivity = 100.0 - 50.0;

  int cinematicModeToggleCooldown = 1;
	int zoomToggleCooldown = 1;
	
  int timesToRepeatZoomCheck = 1;
  int zoomProgress = 0;

  @Override
  public void onInitializeClient() {
    //Register the configuration.
    AutoConfig.register(OkZoomerConfig.class, JanksonConfigSerializer::new);
    OkZoomerConfig config = AutoConfig.getConfigHolder(OkZoomerConfig.class).getConfig();
		
		//Register the keybind.
		KeyBindingRegistry.INSTANCE.register(zoomKeyBinding);

		//Everything related to the zoom is done here.
		ClientTickCallback.EVENT.register(e -> {
      //If Zoom Toggle is enabled, Minecraft is paused and zoom's toggled in, toggle out.
			if (config.zoomToggle) {
        if (minecraft.isPaused() && zoomPressed) {
          zoomPressed = false;
        }
      }

      //If Smooth Camera is enabled, reimplement the Smooth Camera function but with cinematicMode being set.
			if (config.smoothCamera) {
				if (minecraft.options.keySmoothCamera.isPressed()) {
					cinematicMode = toggleBooleanByKeybind(cinematicMode, cinematicModeToggleCooldown);
					cinematicModeToggleCooldown = 3;
				} else {
					cinematicModeToggleCooldown = 1;
        }
				
				//If the zoom is on or Cinematic Mode is on, set to true
        if (zoomProgress == 2 || cinematicMode) {
          minecraft.options.smoothCameraEnabled = true;
				}
				// No "else, set to false" because then it breaks things like Optifine's zoom.
      }

      //If the zoom keybind is pressed, set zoomToggle.
      if (zoomKeyBinding.isPressed()) {
        //If Zoom Toggle is enabled, toggle zoomToggle, else, set it to true.
        if (config.zoomToggle) {
          zoomPressed = toggleBooleanByKeybind(zoomPressed, zoomToggleCooldown);
          zoomToggleCooldown = 3;
        } else {
          zoomPressed = true;
        }
      } else
      //If Zoom Toggle is enabled, reset the cooldown, else, set zoomPressed to false if it was true.
      if (config.zoomToggle) {
        zoomToggleCooldown = 1;
      } else {
        if (zoomPressed) {
          zoomPressed = false;
        }
      }

      //If the keybind is pressed and zoom didn't begin, start zooming.
      if (zoomPressed && zoomProgress == 0) {
        zoomProgress = 1;
      }

      if (zoomProgress == 1) {
        if (zoomPressed) {
          if (!config.smoothTransition || zoomDivisor == 1.0) {
            minecraft.options.fov = realFov * (1.0 / config.zoomDivisor);
          }
          
          if (config.smoothTransition) {
            shouldZoomSmoothly = true;
          }

          //Also set zoomProgress to 2, since it's finished.
          zoomProgress = 2;

          //If "Reduce Sensitivity" is on, set the sensitivity adequately.
          if (config.reduceSensitivity) {
            minecraft.options.mouseSensitivity = realSensitivity * (1.0 / config.zoomDivisor);
          }
          
          //If "Smooth Camera" is on, enable the smooth camera.
          if (config.smoothCamera) {
            minecraft.options.smoothCameraEnabled = true;
          }

          //If "Hide Hands" is on, trigger the mixin with a tick.
          if (config.hideHands) {
            if (!hideHandsBecauseZoom) {
              hideHandsBecauseZoom = true;
              minecraft.gameRenderer.tick();
            }
          }

          //If "Zoom Scrolling" is on, allow for zoom scrolling.
          if (config.zoomScrolling) {
            if (!shouldScrollZoom) {
              shouldScrollZoom = true;
            }
          }
        }
      }

      //If a zoom scroll happened, change the FOV.
      if (config.zoomScrolling && zoomProgress == 2) {
        if ((realFov * (1.0 * config.zoomDivisor)) != minecraft.options.fov) {
          if (!config.smoothTransition) {
            minecraft.options.fov = realFov * (1.0 / config.zoomDivisor);
          }

          if (config.reduceSensitivity) {
            minecraft.options.mouseSensitivity = realSensitivity * (1.0 / config.zoomDivisor);
          }
        }
      }

      //If the zoom isn't pressed and there's still zooming, zoom out.
      if (!zoomPressed && zoomProgress == 2) {
        if (!config.smoothTransition || zoomDivisor == 1.0) {
          minecraft.options.fov = realFov;
        }
        
        if (config.smoothTransition) {
          shouldZoomSmoothly = false;
        }
        
        minecraft.options.mouseSensitivity = realSensitivity;
        zoomProgress = 0;

        if (config.smoothCamera && !cinematicMode) {
          minecraft.options.smoothCameraEnabled = false;
        }

        if (hideHandsBecauseZoom) {
          hideHandsBecauseZoom = false;
          minecraft.gameRenderer.tick();
        }

        if (shouldScrollZoom) {
          shouldScrollZoom = false;
        }

        if (config.zoomScrolling) {
          config.zoomDivisor = zoomDivisor;
        }
      }

      //If there's no zoom going on, set the values to be used by zooming.
      if (zoomProgress == 0) {
        realFov = minecraft.options.fov;
        realSensitivity = minecraft.options.mouseSensitivity;
        zoomDivisor = config.zoomDivisor;
      }
		});
	}
}
