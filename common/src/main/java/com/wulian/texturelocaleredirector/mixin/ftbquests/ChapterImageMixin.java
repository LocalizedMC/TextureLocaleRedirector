package com.wulian.texturelocaleredirector.mixin.ftbquests;

import com.wulian.texturelocaleredirector.LangTextureCache;
import com.wulian.texturelocaleredirector.TextureLocaleRedirector;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.ChapterImage;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Restriction(require = @Condition("ftbquests"))
@Mixin(value = ChapterImage.class, remap = false)
public abstract class ChapterImageMixin {

    @ModifyVariable(
            method = "setImage(Ldev/ftb/mods/ftblibrary/icon/Icon;)Ldev/ftb/mods/ftbquests/quest/ChapterImage;",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Icon injectLocalizedImage(Icon image) {
        String currentLang = LangTextureCache.getCurrentLanguage();

        if ("en_us".equals(currentLang) || image == null) {
            return image;
        }

        Identifier id = Identifier.of(image.toString());
        String path = id.getPath();

        String texturePrefix = "textures/";
        int index = path.indexOf(texturePrefix);
        if (index == -1) return image;

        String before = path.substring(0, index + texturePrefix.length());
        String after  = path.substring(index + texturePrefix.length());

        if (after.startsWith(currentLang + "/")) {
            return image; // 已经带有语言前缀
        }

        String localizedPath = before + currentLang + "/" + after;
        Identifier localizedId = Identifier.of(id.getNamespace(), localizedPath);

        Icon localizedIcon = Icon.getIcon(localizedId.toString());
        if (!localizedIcon.isEmpty()) {
            TextureLocaleRedirector.LOGGER.info("Redirected ChapterImage icon {} -> {}", id, localizedId);
            return localizedIcon;
        }
        return image;
    }
}
