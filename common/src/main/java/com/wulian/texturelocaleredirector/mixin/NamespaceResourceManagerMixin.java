package com.wulian.texturelocaleredirector.mixin;

import com.wulian.texturelocaleredirector.LangTextureCache;
import com.wulian.texturelocaleredirector.TextureLocaleRedirector;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin implements ResourceManager {

    @Inject(method = "findResources", at = @At("RETURN"))
    private void onFindResources(String startingPath, Predicate<Identifier> allowedPathPredicate,
                                 CallbackInfoReturnable<Map<Identifier, Resource>> cir) {

        if ("en_us".equals(LangTextureCache.getCurrentLanguage())) {
            return;
        }

        Map<Identifier, Resource> originalResources = cir.getReturnValue();
        if (originalResources.isEmpty()) {
            return;
        }

        Map<Identifier, Resource> langSpecificResources = new HashMap<>();

        for (Map.Entry<Identifier, Resource> entry : originalResources.entrySet()) {
            Identifier originalId = entry.getKey();
            Identifier langId = LangTextureCache.getLocalizedId(originalId);

            if (langId == null) {
                continue;
            }

            Optional<Resource> langResource = this.checkResourceAndCache(langId, originalId);
            langResource.ifPresent(resource -> langSpecificResources.put(originalId, resource));
        }

        if (!langSpecificResources.isEmpty()) {
            originalResources.putAll(langSpecificResources);
        }
    }

    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    private void onGetResource(Identifier id, CallbackInfoReturnable<Optional<Resource>> cir) {

        Identifier langId = LangTextureCache.getLocalizedId(id);
        if (langId == null) {
            return;
        }

        Optional<Resource> langResource = this.checkResourceAndCache(langId, id);
        if (langResource.isPresent()) {
            cir.setReturnValue(langResource);
        }
    }

    @Unique
    public Optional<Resource> checkResourceAndCache(Identifier langId, Identifier originalId) {
        Boolean cache = LangTextureCache.get(langId);

        if (cache != null) {
            if (cache) {
                return this.getResource(langId);
            } else {
                return Optional.empty();
            }
        }

        Optional<Resource> langResource = this.getResource(langId);

        if (langResource.isPresent()) {
            LangTextureCache.put(langId, true);
            TextureLocaleRedirector.LOGGER.info("Redirected resource {} -> {}", originalId, langId);
            return langResource;
        } else {
            LangTextureCache.put(langId, false);
            return Optional.empty();
        }
    }
}