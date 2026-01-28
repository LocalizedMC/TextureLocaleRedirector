package com.wulian.texturelocaleredirector.mixin;

import com.wulian.texturelocaleredirector.LangTextureCache;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(LanguageManager.class)
public abstract class LanguageManagerMixin {

    @Shadow
    private String currentLanguageCode;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(String languageCode, Consumer<TranslationStorage> reloadCallback, CallbackInfo ci) {
        LangTextureCache.setCurrentLanguage(languageCode);
        LangTextureCache.clear();
    }

    @Inject(method = "setLanguage", at = @At("HEAD"))
    private void onSetLanguage(String languageCode, CallbackInfo ci) {
        LangTextureCache.setCurrentLanguage(languageCode);
        LangTextureCache.clear();
    }

    @Inject(method = "reload", at = @At("HEAD"))
    private void onReload(ResourceManager manager, CallbackInfo ci) {
        LangTextureCache.setCurrentLanguage(currentLanguageCode);
        LangTextureCache.clear();
    }
}
