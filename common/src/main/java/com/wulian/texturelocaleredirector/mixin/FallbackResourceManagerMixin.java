package com.wulian.texturelocaleredirector.mixin;

import com.wulian.texturelocaleredirector.TranslatableTextureCache;
import com.wulian.texturelocaleredirector.TextureLocaleRedirector;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(FallbackResourceManager.class)
public abstract class FallbackResourceManagerMixin implements ResourceManager {

    @Inject(method = "listResources", at = @At("RETURN"))
    private void onListResources(
            String startingPath,
            Predicate<
                    /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*Identifier *//*?}*/
            > allowedPathPredicate,
            CallbackInfoReturnable<
                    Map<
                            /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*Identifier *//*?}*/,
                            Resource
                    >
            > cir
    ) {

        if ("en_us".equals(TranslatableTextureCache.getCurrentLanguage())) {
            return;
        }

        Map<
                /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*Identifier *//*?}*/,
                Resource
        > originalResources = cir.getReturnValue();

        if (originalResources.isEmpty()) {
            return;
        }

        Map<
                /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*Identifier *//*?}*/,
                Resource
        > langSpecificResources = new HashMap<>();

        for (Map.Entry<
                /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*Identifier *//*?}*/,
                Resource
        > entry : originalResources.entrySet()) {

            var originalId = entry.getKey();
            var langId = TranslatableTextureCache.getLocalizedId(originalId);

            if (langId == null) {
                continue;
            }

            Optional<Resource> langResource = this.textureLocaleRedirector$checkResourceAndCache(langId, originalId);
            langResource.ifPresent(resource -> langSpecificResources.put(originalId, resource));
        }

        if (!langSpecificResources.isEmpty()) {
            originalResources.putAll(langSpecificResources);
        }
    }

    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    private void onGetResource(
            /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*Identifier *//*?}*/ id,
            CallbackInfoReturnable<Optional<Resource>> cir
    ) {

        var langId = TranslatableTextureCache.getLocalizedId(id);
        if (langId == null) {
            return;
        }

        Optional<Resource> langResource = this.textureLocaleRedirector$checkResourceAndCache(langId, id);
        if (langResource.isPresent()) {
            cir.setReturnValue(langResource);
        }
    }

    @Unique
    public Optional<Resource> textureLocaleRedirector$checkResourceAndCache(
            /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*Identifier *//*?}*/ langId,
            /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*Identifier *//*?}*/ originalId
    ) {
        Boolean cache = TranslatableTextureCache.get(langId);

        if (cache != null) {
            if (cache) {
                return this.getResource(langId);
            } else {
                return Optional.empty();
            }
        }

        Optional<Resource> langResource = this.getResource(langId);

        if (langResource.isPresent()) {
            TranslatableTextureCache.put(langId, true);
            TextureLocaleRedirector.LOGGER.info("Redirected resource {} -> {}", originalId, langId);
            return langResource;
        } else {
            TranslatableTextureCache.put(langId, false);
            return Optional.empty();
        }
    }
}
