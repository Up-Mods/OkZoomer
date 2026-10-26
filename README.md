# Ok Zoomer

![Banner for the Ok Zoomer mod](https://mod-assets.upcraft.dev/promo/ok-zoomer/modpage_banner.png)

![A Minecraft landscape with cows and pigs being zoomed using Ok Zoomer's zoom, configured at the default Camera preset.](https://mod-assets.upcraft.dev/promo/ok-zoomer/showcase_camera.avif)

What you are seeing here is the best zoom mod around here. It is faster than those who claim to be simple, it is lighter than bloated competitors with megabytes of dependencies, it is powerful with its configurability and its many presets, and most importantly, it is never afraid to innovate, having popularized zoom transitions on its release and always introducing new features.

The zoom key is bound to <kbd>C</kbd> by default, with the zoom itself prioritizing a smooth transition between the user's interface and a clean view, which is perfect for screenshots! But if you want something else? We have options!

By going to Mod Menu or by using the `/ok_zoomer` command, you can choose other presets such as "Competitive" (prioritizes your interface), "Classic" (emulates OptiFine's zoom), Spyglass, and others! You can also tweak other options such as Zoom Transition, Cinematic Camera, Zoom Divisor, and much more!

You can see more information on the options on the [wiki](https://github.com/Up-Mods/OkZoomer/wiki/Configuration).

## Download

This mod is available for download on the following services:

- [Modrinth (recommended)](https://modrinth.com/mod/ok-zoomer)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ok-zoomer)
- [GitHub Releases](https://github.com/Up-Mods/OkZoomer/releases)

## For Developers

Ok Zoomer is available on [Up's Mods Maven](https://maven.uuid.gg/releases/page/langeweile/ok_zoomer/ok_zoomer/) for both Fabric and NeoForge.

```gradle
repositories {
    maven {
        name = "Up's Mods"
        url = "https://maven.uuid.gg/releases"
    }
}
```

```gradle
dependencies {
    implementation("page.langeweile.ok_zoomer:ok_zoomer:<version>") { transitive = false }
}
```

Replace `<version>` with a version from [Up's Mods Maven](https://maven.uuid.gg/releases/page/langeweile/ok_zoomer/ok_zoomer/).

> Do not use Modrinth Maven (`api.modrinth.com/maven`). Its jar does not process Ok Zoomer's bundled dependencies, which crashes on startup.
