package page.langeweile.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.TracingExecutor;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.Executors;

@Mixin(Util.class)
public class UtilMixin {
	@ModifyReturnValue(method = "makeExecutor", at = @At("TAIL"))
	private static TracingExecutor a(TracingExecutor original) {
		return new TracingExecutor(Executors.newVirtualThreadPerTaskExecutor());
	}
}
