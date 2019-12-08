package io.github.joaoh1.okzoomer;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.*;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = "okzoomer")
public class OkZoomerConfig implements ConfigData {
    @Comment("Enables Smooth Camera while zooming, interfers with Cinematic Mode but shouldn't be anything negative.")
    @ConfigEntry.Gui.Tooltip(count = 2)
    boolean smoothCamera = true;
    @Comment("Enables Smooth Transitions when zooming in and out. WARNING: It might be prone to breakage, be careful.")
    @ConfigEntry.Gui.Tooltip(count = 2)
    boolean smoothTransition = false;
    @Comment("The divisor used while applying smoothing, less is faster, more is slower.")
    @ConfigEntry.Gui.Tooltip()
    int smoothDivisor = 32;
    @Comment("Enables the ability to toggle zooming.")
    @ConfigEntry.Gui.Tooltip()
    boolean zoomToggle = false;
    @Comment("The multiplier applied to the FOV when zooming. 1.25 is the recommended maximum,\nanything above that will break the FOV.")
    @ConfigEntry.Gui.Tooltip(count =  3)
    double zoomMultiplier = 0.5;
    
}