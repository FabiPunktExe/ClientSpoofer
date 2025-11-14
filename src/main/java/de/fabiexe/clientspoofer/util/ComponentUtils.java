package de.fabiexe.clientspoofer.util;

import de.fabiexe.clientspoofer.ClientSpooferOptions;
import de.fabiexe.clientspoofer.mixin.LanguageAccessor;
import de.fabiexe.clientspoofer.mixin.client.LanguageManagerAccessor;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

public class ComponentUtils {
    private static final Language language = LanguageAccessor.invokeLoadDefault();
    private static final Map<ClientPacketListener, Language> serverLanguages = new IdentityHashMap<>();

    public static @NotNull String getString(@NotNull Component component) {
        if (component instanceof MutableComponent) {
            StringBuilder stringBuilder = new StringBuilder();
            visit(component.getContents()).ifPresent(stringBuilder::append);
            for (Component sibling : component.getSiblings()) {
                stringBuilder.append(getString(sibling));
            }
            return stringBuilder.toString();
        } else {
            return component.getString();
        }
    }

    public static @NotNull Optional<String> visit(@NotNull ComponentContents contents) {
        if (contents instanceof KeybindContents keybind) {
            if (!canTranslate(keybind.getName())) {
                return Optional.of(keybind.getName());
            }
        } else if (contents instanceof TranslatableContents translatable) {
            if (!canTranslate(translatable.getKey())) {
                return Optional.of(Objects.requireNonNullElseGet(translatable.getFallback(), translatable::getKey));
            }
        }
        return contents.visit(Optional::of);
    }

    public static boolean canTranslate(@NotNull String key) {
        Minecraft minecraft = Minecraft.getInstance();
        ServerData serverData = minecraft.getCurrentServer();
        if (serverData != null && serverData.getResourcePackStatus() == ServerData.ServerPackStatus.ENABLED) {
            if (!serverLanguages.containsKey(minecraft.getConnection())) {
                if (!serverLanguages.isEmpty()) {
                    serverLanguages.clear();
                }
                serverLanguages.put(minecraft.getConnection(), createServerLanguage());
            }

            return serverLanguages.get(minecraft.getConnection()).has(key);
        }

        return language.has(key);
    }

    private static @NotNull Language createServerLanguage() {
        Minecraft minecraft = Minecraft.getInstance();

        List<PackResources> allPackResources = minecraft.getResourceManager().listPacks().toList();
        List<PackResources> packResources = new ArrayList<>();
        packResources.add(allPackResources.getFirst());
        for (int i = 1; i < allPackResources.size(); i++) {
            PackResources packResource = allPackResources.get(i);
            PackSource source = packResource.location().source();
            if (!ClientSpooferOptions.hideMods() ||
                    ClientSpooferOptions.ALLOWED_MODS.contains(packResource.packId()) ||
                    source == PackSource.FEATURE ||
                    source == PackSource.WORLD ||
                    source == PackSource.SERVER) {
                packResources.add(packResource);
            }
        }
        ResourceManager resourceManager = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, packResources);

        String currentLanguageCode = minecraft.getLanguageManager().getSelected();
        LanguageInfo languageInfo;
        Map<String, LanguageInfo> languages = LanguageManagerAccessor.invokeExtractLanguages(resourceManager.listPacks());
        List<String> list = new ArrayList<>(2);
        list.add("en_us");
        boolean bidirectional = LanguageManagerAccessor.getDefaultLanguage().bidirectional();
        if (!currentLanguageCode.equals("en_us") && (languageInfo = languages.get(currentLanguageCode)) != null) {
            list.add(currentLanguageCode);
            bidirectional = languageInfo.bidirectional();
        }
        return ClientLanguage.loadFrom(resourceManager, list, bidirectional);
    }
}
