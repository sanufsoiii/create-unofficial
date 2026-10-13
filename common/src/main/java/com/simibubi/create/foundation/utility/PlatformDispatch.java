package com.simibubi.create.foundation.utility;

import java.lang.reflect.Method;

public class PlatformDispatch {
	private static final boolean IS_NEOFORGE;

	static {
		boolean neoforge = false;
		try {
			Class.forName("net.neoforged.fml.ModList");
			neoforge = true;
		} catch (Throwable ignored) {
		}
		IS_NEOFORGE = neoforge;
	}

	public static boolean isNeoForge() {
		return IS_NEOFORGE;
	}

	private static final java.util.Map<String, Method> METHOD_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

	public static Object call(String neoClass, String fabricClass, String methodName, Class<?>[] paramTypes, Object... args) {
		String targetClass = IS_NEOFORGE ? neoClass : fabricClass;
		try {
			String cacheKey = targetClass + '#' + methodName;
			Method method = METHOD_CACHE.computeIfAbsent(cacheKey, k -> {
				try {
					Class<?> clazz = Class.forName(targetClass);
					Method m = clazz.getMethod(methodName, paramTypes);
					m.setAccessible(true);
					return m;
				} catch (Exception e) {
					throw new RuntimeException("Could not find method " + targetClass + "." + methodName, e);
				}
			});
			return method.invoke(null, args);
		} catch (Throwable t) {
			if (t instanceof RuntimeException re) {
				throw re;
			}
			throw new RuntimeException("Platform dispatch failed for " + targetClass + "." + methodName, t);
		}
	}
}
