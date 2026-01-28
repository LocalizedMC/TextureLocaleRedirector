package com.wulian.texturelocaleredirector.mixin;

import com.wulian.texturelocaleredirector.LangTextureCache;
import com.wulian.texturelocaleredirector.TextureLocaleRedirector;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
//? if >=1.21.11 {
import net.minecraft.resources.Identifier;
//?} else if >=1.20.6 && <=1.21.10 {
/*import net.minecraft.resources.ResourceLocation;
*///?}
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
    private void onFindResources(
            String startingPath,
            Predicate<
                    /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/
            > allowedPathPredicate,
            CallbackInfoReturnable<
                    Map<
                            /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/,
                            Resource
                    >
            > cir
    ) {

        if ("en_us".equals(LangTextureCache.getCurrentLanguage())) {
            return;
        }

        Map<
                /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/,
                Resource
        > originalResources = cir.getReturnValue();

        if (originalResources.isEmpty()) {
            return;
        }

        Map<
                /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/,
                Resource
        > langSpecificResources = new HashMap<>();

        for (Map.Entry<
                /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/,
                Resource
        > entry : originalResources.entrySet()) {

            var originalId = entry.getKey();
            var langId = LangTextureCache.getLocalizedId(originalId);

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
    private void onGetResource(
            /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/ id,
            CallbackInfoReturnable<Optional<Resource>> cir
    ) {

        var langId = LangTextureCache.getLocalizedId(id);
        if (langId == null) {
            return;
        }

        Optional<Resource> langResource = this.checkResourceAndCache(langId, id);
        if (langResource.isPresent()) {
            cir.setReturnValue(langResource);
        }
    }

    @Unique
    public Optional<Resource> checkResourceAndCache(
            /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/ langId,
            /*? if >=1.21.11 {*/ Identifier /*?} else {*/ /*ResourceLocation*/ /*?}*/ originalId
    ) {
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
