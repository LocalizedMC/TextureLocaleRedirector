package com.wulian.texturelocaleredirector.mixin;

import com.wulian.texturelocaleredirector.TranslatableTextureCache;
//? if >=1.21.1 {
import net.minecraft.client.resources.language.ClientLanguage;
//?}
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.21.1 {
import java.util.function.Consumer;
//?}

@Mixin(LanguageManager.class)
public abstract class LanguageManagerMixin {

    @Shadow
    private String currentCode;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(String languageCode /*? if >=1.21.1 {*/, Consumer<ClientLanguage> reloadCallback/*?}*/, CallbackInfo ci) {
        TranslatableTextureCache.setCurrentLanguage(languageCode);
        TranslatableTextureCache.clear();
    }

    @Inject(method = "setSelected", at = @At("HEAD"))
    private void onSetLanguage(String languageCode, CallbackInfo ci) {
        TranslatableTextureCache.setCurrentLanguage(languageCode);
        TranslatableTextureCache.clear();
    }

    @Inject(method = "onResourceManagerReload", at = @At("HEAD"))
    private void onReload(ResourceManager manager, CallbackInfo ci) {
        TranslatableTextureCache.setCurrentLanguage(currentCode);
        TranslatableTextureCache.clear();
    }
}
