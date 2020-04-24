package io.github.joaoh1.okzoomer;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class OkZoomerMod implements ClientModInitializer {
	public static final FabricKeyBinding zoomKeyBinding = FabricKeyBinding.Builder
		.create(new Identifier("okzoomer", "zoom"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.misc")
		.build();

	@Override
	public void onInitializeClient() {
		// TODO - Actually do zoom stuff
		System.out.println("owo what's this");
		KeyBindingRegistry.INSTANCE.register(zoomKeyBinding);
	}
}
