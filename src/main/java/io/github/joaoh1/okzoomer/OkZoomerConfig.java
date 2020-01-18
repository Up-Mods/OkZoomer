package io.github.joaoh1.okzoomer;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.*;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = "okzoomer")
@Config.Gui.Background(value = "minecraft:textures/block/yellow_wool.png")
public class OkZoomerConfig implements ConfigData {
  @Comment("Enables Smooth Camera while zooming, interferes with Cinematic Mode but it shouldn't be anything negative.")
  @ConfigEntry.Gui.Tooltip(count = 2)
  boolean smoothCamera = true;

  @Comment("While enabled, zooming hides your hands.")
  @ConfigEntry.Gui.Tooltip(count = 1)
  boolean hideHands = true;

  @Comment("Enables Smooth Transitions when zooming in and out.")
  @ConfigEntry.Gui.Tooltip(count = 1)
  boolean smoothTransition = false;

  @ConfigEntry.Gui.CollapsibleObject
  @ConfigEntry.Gui.Tooltip(count = 1)
  AdvancedSmoothTransSettings advancedSmoothTransSettings = new AdvancedSmoothTransSettings();

  public static class AdvancedSmoothTransSettings {
    @Comment("The divisor used while applying smoothing, smaller number zooms faster, bigger number zoom slower.")
    @ConfigEntry.Gui.Tooltip(count = 2)
    int smoothDivisor = 128;

    @Comment("The number of times the transition is applied during the zoom press.")
    @ConfigEntry.Gui.Tooltip(count = 1)
    int timesToRepeatSmoothing = 5;

    @Comment("The multiplier used on the FOV's smoothing while it's being zoomed in.")
    @ConfigEntry.Gui.Tooltip(count = 1)
    double transitionStartMultiplier = 2;

    @Comment("The multiplier used on the FOV's smoothing while it's being zoomed out.")
    @ConfigEntry.Gui.Tooltip(count = 1)
    double transitionEndMultiplier = 2;
  }

  @Comment("Enables the ability to toggle zooming.")
  @ConfigEntry.Gui.Tooltip()
  boolean zoomToggle = false;

  @Comment("Reduces the mouse sensitivity when zooming.")
  @ConfigEntry.Gui.Tooltip()
  boolean reduceSensitivity = false;

  @Comment("The multiplier applied to the FOV when zooming.\n1.25 is the recommended maximum, anything above that will break the FOV.")
  @ConfigEntry.Gui.Tooltip(count =  3)
  double zoomMultiplier = 0.25;  
}