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
  private static MinecraftClient minecraft = MinecraftClient.getInstance();

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

  //Increases or decreases the zoom, used by zoom scrolling.
  private static double zoomScrollTarget = 0.0;
  public static void scrollZoom(double scrollAmount) {
    if (scrollAmount > 0.0D) {
      scrollAmount = 0.25;
    } else if (scrollAmount < 0.0D) {
      scrollAmount = -0.25;
    }

    zoomScrollTarget += scrollAmount;
  }

  //Used by the mixin that hides the hands.
  private static boolean hideHandsBecauseZoom = false;
  public static boolean shouldHideHands() {
    return hideHandsBecauseZoom;
  }

  //Used by the mixin that overrides the scrolling.
  private static boolean shouldScrollZoom = false;
  public static boolean shouldScrollZoom() {
    return shouldScrollZoom;
  }

	//Internal booleans, one for mimicking Cinematic Mode, another replaces zoomKeybinding.isPressed().
  boolean cinematicMode = false;
  boolean zoomPressed = false;
	
  double realFov = 70.0;
  double realSensitivity = 100.0;
  double smoothing = 1.0 - 0.5;
  double sensitivity = 100.0 - 50.0;
  double zoomMultiplier = 1 / 4;
  double zoomDivisorInteger = 4;
  double zoomDivisorToRound = 0;
  double zoomScrollDivisor = 4.0;

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
      //Set the zoom multiplier, which will be used on the FOV when zooming.
      if (config.zoomScrolling) {
        if (zoomMultiplier != (1.0 / (zoomDivisorInteger + zoomDivisorToRound))) {
          String[] zoomDivisorArray = Double.toString(config.zoomDivisor).split("[.]", 2);
          zoomDivisorInteger = Double.parseDouble(zoomDivisorArray[0]);
          zoomDivisorToRound = Double.parseDouble(zoomDivisorArray[1]);
          if (zoomDivisorToRound >= 0 && zoomDivisorToRound < 25) zoomDivisorToRound = 0.00;
          if (zoomDivisorToRound >= 25 && zoomDivisorToRound < 50) zoomDivisorToRound = 0.25;
          if (zoomDivisorToRound >= 50 && zoomDivisorToRound < 75) zoomDivisorToRound = 0.50;
          if (zoomDivisorToRound >= 75 && zoomDivisorToRound < 100) zoomDivisorToRound = 0.75;
          zoomMultiplier = 1.0 / (zoomDivisorInteger + zoomDivisorToRound);
          zoomScrollDivisor = zoomDivisorInteger + zoomDivisorToRound;
        }
      } else {
        if (zoomMultiplier != 1.0 / config.zoomDivisor) {
          zoomMultiplier = 1.0 / config.zoomDivisor;
        }
      }

      //If Zoom Toggle is enabled, Minecraft is paused and zoom's toggled in, toggle out.
			if (config.zoomToggle) {
        if (minecraft.isPaused() && zoomPressed) {
          zoomPressed = false;
          zoomKeyBinding.setPressed(true);
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
      
      //If Smooth Transitions are enabled, repeat the code more than once to guarantee smoothiness.
      if (config.smoothTransition) {
        timesToRepeatZoomCheck = config.advancedSmoothTransSettings.timesToRepeatSmoothing;
      } else {
        timesToRepeatZoomCheck = 1;
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

      //Repeat X times to guarantee smoothiness.
      for (int i = 0; i < timesToRepeatZoomCheck; i++) {
				//If the keybind is pressed and zoom didn't begin, start zooming.
        if (zoomPressed && zoomProgress == 0) {
          zoomProgress = 1;
        }

        if (zoomProgress == 1) {
          if (zoomPressed) {
						//Multiply smoothing by the defined value in the config.
            smoothing *= config.advancedSmoothTransSettings.transitionStartMultiplier;

						//If Smooth Transitions are disabled, set smoothing to the zoom multiplier.
						//Which in turn, will trigger the next if condition which will finish the zoom.
            if (!config.smoothTransition || zoomMultiplier == 1.0) {
              smoothing = zoomMultiplier;
            }

						//If the smoothing is equal/bigger than the zoom multiplier,
            if (smoothing >= zoomMultiplier) {
							//Set all the values to the zoomed in values.
              smoothing = zoomMultiplier / config.advancedSmoothTransSettings.smoothDivisor;
              minecraft.options.fov = realFov * zoomMultiplier;
							//Also set zoomProgress to 2, since it's finished.
              zoomProgress = 2;

							//If "Reduce Sensitivity" is on, set the sensitivity adequately.
							if (config.reduceSensitivity) {
                minecraft.options.mouseSensitivity = realSensitivity * zoomMultiplier;
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
						} else 
						//Apply the proper smoothing.
						if (zoomMultiplier > 1.0) {
              minecraft.options.fov = realFov * (1.0 + smoothing);
              if (config.reduceSensitivity) {
                minecraft.options.mouseSensitivity = realSensitivity * (1.0 + smoothing);
              }
            } else {
              minecraft.options.fov = realFov * (1.0 - smoothing);
              if (config.reduceSensitivity) {
                minecraft.options.mouseSensitivity = realSensitivity * (1.0 - smoothing);
              }
            }
          }
        }

        //If a zoom scroll happened, change the FOV.
        if (config.zoomScrolling && zoomProgress == 2) {
          if (zoomScrollTarget != 0.0) {
            if (zoomScrollDivisor <= 1.0 && zoomScrollTarget <= 0.0) {
              zoomScrollTarget = 0.0;
              zoomScrollDivisor = 1.0;
              return;
            }
            if (zoomScrollDivisor >= 20.0 && zoomScrollTarget >= 0.0) {
              zoomScrollTarget = 0.0;
              zoomScrollDivisor = 20.0;
              return;
            }

            zoomScrollDivisor += zoomScrollTarget;
            minecraft.options.fov = realFov * (1.0 / zoomScrollDivisor);
            zoomScrollTarget = 0.0;
          }
        }

				//If the zoom isn't pressed and there's still zooming, zoom out.
        if (!zoomPressed && zoomProgress == 2) {
					//The smoothing here is applied similarly to the zoom in.
          smoothing *= config.advancedSmoothTransSettings.transitionEndMultiplier;

          if (!config.smoothTransition || zoomMultiplier == 1.0) {
            smoothing = zoomMultiplier;
          }

          if (smoothing >= zoomMultiplier) {
            smoothing = zoomMultiplier / config.advancedSmoothTransSettings.smoothDivisor;
            minecraft.options.fov = realFov;
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
          } else if (zoomMultiplier > 1.0) {
            minecraft.options.fov = realFov * (1.0 + smoothing);
            if (config.reduceSensitivity) {
              minecraft.options.mouseSensitivity = realSensitivity * (1.0 + smoothing);
            }
          } else {
            minecraft.options.fov = realFov * (1.0 - smoothing);
            if (config.reduceSensitivity) {
              minecraft.options.mouseSensitivity = realSensitivity * (1.0 - smoothing);
            }
          }
        }

				//If there's no zoom going on, set the values to be used by zooming.
        if (zoomProgress == 0) {
          smoothing = zoomMultiplier / config.advancedSmoothTransSettings.smoothDivisor;
          realFov = minecraft.options.fov;
          realSensitivity = minecraft.options.mouseSensitivity;
          zoomScrollDivisor = zoomDivisorInteger + zoomDivisorToRound;
        }
      }
		});
	}
}
