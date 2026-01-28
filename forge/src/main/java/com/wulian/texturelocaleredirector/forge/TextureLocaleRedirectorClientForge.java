package com.wulian.texturelocaleredirector.forge;

import com.wulian.texturelocaleredirector.TextureLocaleRedirector;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod(value = TextureLocaleRedirector.MOD_ID)
public class TextureLocaleRedirectorClientForge {
    public TextureLocaleRedirectorClientForge() {
        ModContainer container = ModList.get().getModContainerById(TextureLocaleRedirector.MOD_ID).orElseThrow();
        container.registerExtensionPoint(DisplayTest.class, () -> new DisplayTest(() -> DisplayTest.IGNORESERVERONLY, (a, b) -> true));
    }
}