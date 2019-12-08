package io.github.joaoh1.okzoomer;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.*;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = "okzoomer")
public class OkZoomerConfig implements ConfigData {
    @Comment("Enables Smooth Camera while zooming, interfers with Cinematic Mode but it shouldn't be anything negative.")
    @ConfigEntry.Gui.Tooltip(count = 2)
    boolean smoothCamera = true;
    @Comment("Enables Smooth Transitions when zooming in and out.\nWARNING: It might be prone to breakage, be careful.")
    @ConfigEntry.Gui.Tooltip(count = 2)
    boolean smoothTransition = false;
    @Comment("The divisor used while applying smoothing, smaller number zooms faster, bigger number zoom slower.")
    @ConfigEntry.Gui.Tooltip(count = 2)
    int smoothDivisor = 64;
    @Comment("Enables the ability to toggle zooming.")
    @ConfigEntry.Gui.Tooltip()
    boolean zoomToggle = false;
    @Comment("The multiplier applied to the FOV when zooming.\n1.25 is the recommended maximum, anything above that will break the FOV.")
    @ConfigEntry.Gui.Tooltip(count =  3)
    double zoomMultiplier = 0.5;
    
}