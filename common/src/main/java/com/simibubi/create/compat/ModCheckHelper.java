package com.simibubi.create.compat;

public class ModCheckHelper {
	public static boolean isModLoaded(String modId) {
		try {
			Class<?> impl = Class.forName("com.simibubi.create.compat.ModCheckHelperImpl");
			return (boolean) impl.getMethod("isModLoaded", String.class).invoke(null, modId);
		} catch (Throwable t) {
			return false;
		}
	}
}
