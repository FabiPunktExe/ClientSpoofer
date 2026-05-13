package de.fabiexe.clientspoofer.gui;

import de.fabiexe.clientspoofer.ClientSpoofer;
import de.fabiexe.clientspoofer.ClientSpooferOptions;
import de.fabiexe.clientspoofer.SpoofMode;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.minecraft.network.chat.CommonComponents.OPTION_OFF;
import static net.minecraft.network.chat.CommonComponents.OPTION_ON;

public class ClientSpooferOptionsScreen extends Screen {
    private final static Component OPTION_SPOOF_MODE = Component.translatable("clientspoofer.option.spoof_mode");
    private final static Component OPTION_CUSTOM_CLIENT = Component.translatable("clientspoofer.option.custom_client");
    private final static Component OPTION_PREVENT_FINGERPRINTING = Component.translatable("clientspoofer.option.prevent_fingerprinting");
    private final static Component OPTION_HIDE_MODS = Component.translatable("clientspoofer.option.hide_mods");
    private final static Component OPTION_ALLOWED_MODS = Component.translatable("clientspoofer.option.allowed_mods");
    private final static Component OPTION_FILTER = Component.translatable("clientspoofer.option.filter");
    private final static Component OPTION_DISABLE_CUSTOM_PAYLOADS = Component.translatable("clientspoofer.option.disable_custom_payloads");
    private final static Component OPTION_ALLOWED_CUSTOM_PAYLOAD_CHANNELS = Component.translatable("clientspoofer.option.allowed_custom_payload_channels");
    private final Screen previous;
    private String modSearch = "";
    private ModAllowList modAllowList = null;

    public ClientSpooferOptionsScreen(Screen previous) {
        super(Component.translatable("clientspoofer.options.title"));
        this.previous = previous;
    }

    @Override
    protected void init() {
        ClientSpooferOptions options = ClientSpoofer.getOptions();
        SpoofMode spoofMode = options.getSpoofMode();
        List<AbstractWidget> widgets = new ArrayList<>();

        // Spoof mode
        widgets.add(Button.builder(OPTION_SPOOF_MODE.copy().append(": ").append(spoofModeToComponent(spoofMode)), _ -> {
            ClientSpoofer.getOptions().setSpoofMode(toggleSpoofMode(spoofMode));
            rebuildWidgets();
        }).build());

        // Custom brand
        if (spoofMode == SpoofMode.CUSTOM) {
            widgets.add(new MultiLineTextWidget(OPTION_CUSTOM_CLIENT.copy().withStyle(ChatFormatting.GRAY), font).setMaxWidth(Button.DEFAULT_WIDTH));
            EditBox customClientEditBox = new EditBox(font, 0, -5, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.empty());
            customClientEditBox.setValue(options.getCustomClient());
            customClientEditBox.setResponder(options::setCustomClient);
            widgets.add(customClientEditBox);
        }

        // Prevent fingerprinting
        if (spoofMode == SpoofMode.CUSTOM) {
            widgets.add(Button.builder(OPTION_PREVENT_FINGERPRINTING.copy().append(": ").append(options.isPreventFingerprinting() ? OPTION_ON : OPTION_OFF), _ -> {
                options.setPreventFingerprinting(!options.isPreventFingerprinting());
                rebuildWidgets();
            }).build());
        }

        // Hide mods
        if (spoofMode == SpoofMode.MODDED || spoofMode == SpoofMode.CUSTOM) {
            widgets.add(Button.builder(OPTION_HIDE_MODS.copy().append(": ").append(options.isHideMods() ? OPTION_ON : OPTION_OFF), _ -> {
                options.setHideMods(!options.isHideMods());
                rebuildWidgets();
            }).build());
        }

        // Allowed mods
        if (spoofMode == SpoofMode.MODDED || spoofMode == SpoofMode.CUSTOM) {
            widgets.add(new MultiLineTextWidget(0, 10, OPTION_ALLOWED_MODS, font).setMaxWidth(200));

            widgets.add(new MultiLineTextWidget(OPTION_FILTER.copy().withStyle(ChatFormatting.GRAY), font).setMaxWidth(200));

            EditBox searchEditBox = new EditBox(font, 0, -5, 250, 20, Component.literal(""));
            searchEditBox.setValue(modSearch);
            searchEditBox.setResponder(value -> {
                modSearch = value;
                fillModAllowList();
            });
            widgets.add(searchEditBox);

            modAllowList = new ModAllowList(minecraft, 250, 100, 0);
            fillModAllowList();
            widgets.add(modAllowList);
        }

        // Disable custom payloads
        if (spoofMode == SpoofMode.CUSTOM) {
            widgets.add(Button.builder(OPTION_DISABLE_CUSTOM_PAYLOADS.copy().append(": ").append(options.isDisableCustomPayloads() ? OPTION_ON : OPTION_OFF), _ -> {
                options.setDisableCustomPayloads(!options.isDisableCustomPayloads());
                rebuildWidgets();
            }).pos(0, 10).build());
        }

        // Allowed custom payload channels
        if (spoofMode == SpoofMode.CUSTOM) {
            widgets.add(new MultiLineTextWidget(0, 10, OPTION_ALLOWED_CUSTOM_PAYLOAD_CHANNELS, font).setMaxWidth(200));

            MultiLineEditBox editBox = MultiLineEditBox.builder().build(font, 200, 100, OPTION_ALLOWED_CUSTOM_PAYLOAD_CHANNELS);
            editBox.setValue(String.join("\n", options.getAllowedCustomPayloadChannels()));
            editBox.setValueListener(value -> {
                String[] channels = value.split("\n");
                options.setAllowedCustomPayloadChannels(new HashSet<>());
                for (String channel : channels) {
                    String trimmed = channel.trim();
                    if (!trimmed.isBlank()) {
                        Set<String> allowedChannels = new HashSet<>(options.getAllowedCustomPayloadChannels());
                        allowedChannels.add(trimmed);
                        options.setAllowedCustomPayloadChannels(allowedChannels);
                    }
                }
            });
            widgets.add(editBox);
        }

        // Done
        widgets.add(Button.builder(Component.translatable("gui.done"), _ -> onClose()).build());

        int y = 5;
        for (AbstractWidget widget : widgets) {
            y += widget.getY();
            widget.setPosition((width - widget.getWidth()) / 2, y);
            y += widget.getHeight() + 5;
            addRenderableWidget(widget);
        }
    }

    private @NotNull Component spoofModeToComponent(SpoofMode spoofMode) {
        return switch (spoofMode) {
            case VANILLA -> Component.translatable("clientspoofer.option.spoof_mode.vanilla");
            case MODDED -> Component.translatable("clientspoofer.option.spoof_mode.modded");
            case CUSTOM -> Component.translatable("clientspoofer.option.spoof_mode.custom");
            case OFF -> OPTION_OFF;
        };
    }

    private @NotNull SpoofMode toggleSpoofMode(SpoofMode spoofMode) {
        return switch (spoofMode) {
            case VANILLA -> SpoofMode.MODDED;
            case MODDED -> SpoofMode.CUSTOM;
            case CUSTOM -> SpoofMode.OFF;
            case OFF -> SpoofMode.VANILLA;
        };
    }

    @Override
    public void onClose() {
        ClientSpoofer.saveOptions();
        Minecraft.getInstance().setScreen(previous);
    }

    private void fillModAllowList() {
        modAllowList.clearEntries();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            if (mod.getMetadata().getType().equals("builtin")) {
                continue;
            }

            boolean matches = true;
            for (String term : modSearch.split(" ")) {
                if (!term.isBlank() &&
                        !mod.getMetadata().getName().toLowerCase().contains(term.toLowerCase()) &&
                        !mod.getMetadata().getId().toLowerCase().contains(term.toLowerCase())) {
                    matches = false;
                }
            }

            if (matches) {
                modAllowList.addEntry(new ModAllowEntry(mod));
            }
        }
    }

    private class ModAllowList extends AbstractSelectionList<ModAllowEntry> {
        public ModAllowList(Minecraft minecraft, int width, int height, int y) {
            super(minecraft, width, height, y, 21);
        }

        @Override
        protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {}

        @Override
        protected int addEntry(@NonNull ModAllowEntry entry) {
            return super.addEntry(entry);
        }

        @Override
        protected void clearEntries() {
            super.clearEntries();
        }
    }

    private class ModAllowEntry extends ContainerObjectSelectionList.Entry<ModAllowEntry> {
        private final Checkbox checkbox;

        public ModAllowEntry(@NotNull ModContainer mod) {
            ClientSpooferOptions options = ClientSpoofer.getOptions();
            checkbox = Checkbox.builder(Component.literal(mod.getMetadata().getName()), font)
                    .selected(options.getAllowedMods().contains(mod.getMetadata().getId()))
                    .onValueChange((_, value) -> {
                        String modId = mod.getMetadata().getId();
                        Set<String> allowedMods = new HashSet<>(options.getAllowedMods());
                        if (value) {
                            allowedMods.add(modId);
                        } else {
                            allowedMods.remove(modId);
                        }
                        options.setAllowedMods(allowedMods);
                    })
                    .build();
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(checkbox);
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float delta) {
            checkbox.setPosition(getContentX() + 11, getContentY());
            checkbox.extractContents(graphics, mouseX, mouseY, delta);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(checkbox);
        }
    }
}
