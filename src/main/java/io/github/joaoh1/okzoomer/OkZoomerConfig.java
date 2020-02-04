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

  @Comment("Enables the ability to toggle zooming.")
  @ConfigEntry.Gui.Tooltip()
  boolean zoomToggle = false;

  @Comment("Reduces the mouse sensitivity when zooming.")
  @ConfigEntry.Gui.Tooltip()
  boolean reduceSensitivity = false;

  @Comment("Allows to increase or decrease zoom by scrolling.")
  @ConfigEntry.Gui.Tooltip()
  boolean zoomScrolling = false;

  @Comment("The divisor applied to the FOV when zooming.")
  @ConfigEntry.Gui.Tooltip(count =  1)
  public double zoomDivisor = 4.0;  
}