package page.langeweile.ok_zoomer.utils;

import net.minecraft.sounds.SoundEvent;

import java.lang.reflect.InvocationTargetException;

public class Portals {
	public static SoundEvent getZoomInSound() {
		return getFromPortal("getZoomInSound");
	}

	public static SoundEvent getZoomOutSound() {
		return getFromPortal("getZoomOutSound");
	}

	public static SoundEvent getScrollSound() {
		return getFromPortal("getScrollSound");
	}

	public static <T> T getFromPortal(String method) {
		try {
			var fabricClass = getClass("page.langeweile.ok_zoomer.utils.FabricPortals");
			if (fabricClass != null) {
				return (T) fabricClass.getMethod(method).invoke(null);
			} else {
				var forgeClass = getClass("page.langeweile.ok_zoomer.utils.ForgePortals");
				if (forgeClass != null) {
					return (T) forgeClass.getMethod(method).invoke(null);
				}
			}

			return null;
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}
