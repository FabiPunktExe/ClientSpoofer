package de.fabiexe.clientspoofer.mixin.client;

import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LanguageManager.class)
public interface LanguageManagerAccessor {
    @Accessor("DEFAULT_LANGUAGE")
    static LanguageInfo getDefaultLanguage() {
        throw new UnsupportedOperationException();
    }

    @Invoker
    static Map<String, LanguageInfo> invokeExtractLanguages(Stream<PackResources> stream) {
        throw new UnsupportedOperationException();
    }
}
