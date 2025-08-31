package com.wulian.texturelocaleredirector.mixin;

import com.wulian.texturelocaleredirector.LangTextureCache;
import com.wulian.texturelocaleredirector.TextureLocaleRedirector;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin {

    @Inject(method = "findResources", at = @At("RETURN"))
    private void onFindResources(String startingPath, Predicate<Identifier> allowedPathPredicate,
                                 CallbackInfoReturnable<Map<Identifier, Resource>> cir) {

        String currentLang = LangTextureCache.getCurrentLanguage();

        if ("en_us".equals(currentLang)) {
            return;
        }

        Map<Identifier, Resource> originalResources = cir.getReturnValue();
        if (originalResources.isEmpty()) return;

        Map<Identifier, Resource> langSpecificResources = new HashMap<>();
        String texturePrefix = "textures/";

        for (Map.Entry<Identifier, Resource> entry : originalResources.entrySet()) {
            Identifier originalId = entry.getKey();

            if (!allowedPathPredicate.test(originalId)) {
                continue;
            }

            String originalPath = originalId.getPath();
            int index = originalPath.indexOf(texturePrefix) + texturePrefix.length();

            String before = originalPath.substring(0, index);
            String after = originalPath.substring(index);

            // 避免重复，如zh_cn/zh_cn
            if (after.startsWith(currentLang + "/")) {
                continue;
            }

            String langSpecificPath = before + currentLang + '/' + after;
            Identifier langId = Identifier.of(originalId.getNamespace(), langSpecificPath);

            Boolean cache = LangTextureCache.get(langId);
            if (cache != null) {
                if (cache) {
                    try {
                        ((ResourceManager) this).getResource(langId).ifPresent(resource -> {
                            langSpecificResources.put(originalId, resource);
                            TextureLocaleRedirector.LOGGER.info("Using cached localized texture: {}", langId);
                        });
                    } catch (Exception ignored) {}
                }
                continue;
            }

            try {
                Optional<Resource> langResource = ((ResourceManager) this).getResource(langId);
                if (langResource.isPresent()) {
                    langSpecificResources.put(originalId, langResource.get());
                    LangTextureCache.put(langId, true);
                    TextureLocaleRedirector.LOGGER.info("Found and cached localized texture: {}", langId);
                } else {
                    LangTextureCache.put(langId, false);
                }
            } catch (Exception e) {
                LangTextureCache.put(langId, false);
                TextureLocaleRedirector.LOGGER.warn("Failed to load localized texture: {}", langId, e);
            }
        }

        if (!langSpecificResources.isEmpty()) {
            originalResources.putAll(langSpecificResources);
        }
    }
}