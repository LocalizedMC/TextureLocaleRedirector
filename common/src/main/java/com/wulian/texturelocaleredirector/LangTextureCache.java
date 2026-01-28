package com.wulian.texturelocaleredirector;

//? if >=1.21.11 {
import net.minecraft.resources.Identifier;
//?} else if >=1.20.6 && <=1.21.10 {
/*import net.minecraft.resources.ResourceLocation;
*///?}

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LangTextureCache {

    private static final Map<
            /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/
            , SoftReference<Boolean>
    > existsCache = new ConcurrentHashMap<>();

    private static volatile String currentLanguage = "en_us";

    public static Boolean get(
            /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/ id
    ) {
        SoftReference<Boolean> ref = existsCache.get(id);
        return ref != null ? ref.get() : null;
    }

    public static void put(
            /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/ id,
            boolean exists
    ) {
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

    public static
    /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/
    getLocalizedId(
            /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/ originalId
    ) {
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

        //? if >=1.21.11 {
        return Identifier.of(originalId.getNamespace(), localizedPath);
        //?} else {
        /*return new ResourceLocation(originalId.getNamespace(), localizedPath);
        *///?}
    }
}
