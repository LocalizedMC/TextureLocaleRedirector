package com.wulian.texturelocaleredirector;

import net.minecraft.resources.Identifier;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TranslatableTextureCache {

    private static final Map<Identifier, SoftReference<Boolean>> existsCache = new ConcurrentHashMap<>();

    private static volatile String currentLanguage = "en_us";

    public static Boolean get(Identifier id) {
        SoftReference<Boolean> ref = existsCache.get(id);
        return ref != null ? ref.get() : null;
    }

    public static void put(Identifier id, boolean exists) {
        existsCache.put(id, new SoftReference<>(exists));
    }

    public static void clear() {
        existsCache.clear();
    }

    public static void setCurrentLanguage(String lang) {
        currentLanguage = lang;
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    public static Identifier getLocalizedId(Identifier originalId) {
        String lang = currentLanguage;

        String originalPath = originalId.getPath();

        String texturePrefix = "textures/";
        int index = originalPath.indexOf(texturePrefix);
        if (index == -1) return null;

        String before = originalPath.substring(0, index + texturePrefix.length());
        String after  = originalPath.substring(index + texturePrefix.length());
        
        // Avoid duplicate redirection textures/zh_cn/zh_cn/...
        if (after.startsWith(lang + "/")) {
            return null;
        }

        String localizedPath = before + lang + "/" + after;

        //? if >=1.21.1 {
        return Identifier.fromNamespaceAndPath(originalId.getNamespace(), localizedPath);
        //?} else {
        /*return new Identifier(originalId.getNamespace(), localizedPath);
        *///?}
    }
}
