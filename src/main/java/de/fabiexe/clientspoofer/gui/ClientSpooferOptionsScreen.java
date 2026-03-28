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
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.network.chat.CommonComponents.OPTION_OFF;
import static net.minecraft.network.chat.CommonComponents.OPTION_ON;

public class ClientSpooferOptionsScreen extends Screen {
    private final Screen previous;
    private String modSearch = "";
    private ModAllowList modAllowList = null;

    public ClientSpooferOptionsScreen(Screen previous) {
        super(Component.translatable("clientspoofer.options.title"));
        this.previous = previous;
    }

    @Override
    protected void init() {
        SpoofMode spoofMode = ClientSpooferOptions.SPOOF_MODE;
        List<AbstractWidget> widgets = new ArrayList<>();

        // Spoof mode
        MutableComponent spoofModeButtonText = Component.translatable("clientspoofer.option.spoof_mode").append(": ");
        spoofModeButtonText.append(switch (spoofMode) {
            case VANILLA -> Component.translatable("clientspoofer.option.spoof_mode.vanilla");
            case MODDED -> Component.translatable("clientspoofer.option.spoof_mode.modded");
            case CUSTOM -> Component.translatable("clientspoofer.option.spoof_mode.custom");
            case OFF -> OPTION_OFF;
        });
        widgets.add(Button.builder(spoofModeButtonText, _ -> {
            ClientSpooferOptions.SPOOF_MODE = switch (spoofMode) {
                case VANILLA -> SpoofMode.MODDED;
                case MODDED -> SpoofMode.CUSTOM;
                case CUSTOM -> SpoofMode.OFF;
                case OFF -> SpoofMode.VANILLA;
            };
            rebuildWidgets();
        }).size(200, 20).build());

        // Custom client
        if (spoofMode == SpoofMode.CUSTOM) {
            widgets.add(new MultiLineTextWidget(
                    Component.translatable("clientspoofer.option.custom_client").withStyle(ChatFormatting.GRAY),
                    font).setMaxWidth(200));

            EditBox customClientEditBox = new EditBox(font, 0, -5, 200, 20, Component.literal(""));
            customClientEditBox.setValue(ClientSpooferOptions.CUSTOM_CLIENT);
            customClientEditBox.setResponder(value -> ClientSpooferOptions.CUSTOM_CLIENT = value);
            widgets.add(customClientEditBox);
        }

        // Prevent fingerprinting
        if (spoofMode == SpoofMode.CUSTOM) {
            Component text = Component.translatable("clientspoofer.option.prevent_fingerprinting")
                    .append(": ")
                    .append(ClientSpooferOptions.PREVENT_FINGERPRINTING ? OPTION_ON : OPTION_OFF);
            widgets.add(Button.builder(text, _ -> {
                ClientSpooferOptions.PREVENT_FINGERPRINTING = !ClientSpooferOptions.PREVENT_FINGERPRINTING;
                rebuildWidgets();
            }).size(200, 20).build());
        }

        // Hide mods
        if (spoofMode == SpoofMode.CUSTOM) {
            Component text = Component.translatable("clientspoofer.option.hide_mods")
                    .append(": ")
                    .append(ClientSpooferOptions.HIDE_MODS ? OPTION_ON : OPTION_OFF);
            widgets.add(Button.builder(text, _ -> {
                ClientSpooferOptions.HIDE_MODS = !ClientSpooferOptions.HIDE_MODS;
                rebuildWidgets();
            }).size(200, 20).build());
        }

        // Allowed mods
        if (spoofMode == SpoofMode.MODDED || spoofMode == SpoofMode.CUSTOM) {
            widgets.add(new MultiLineTextWidget(
                    0, 10,
                    Component.translatable("clientspoofer.option.allowed_mods"),
                    font).setMaxWidth(200));

            widgets.add(new MultiLineTextWidget(
                    Component.translatable("clientspoofer.option.filter").withStyle(ChatFormatting.GRAY),
                    font).setMaxWidth(200));

            EditBox searchEditBox = new EditBox(font, 0, -5, 200, 20, Component.literal(""));
            searchEditBox.setValue(modSearch);
            searchEditBox.setResponder(value -> {
                modSearch = value;
                fillModAllowList();
            });
            widgets.add(searchEditBox);

            modAllowList = new ModAllowList(minecraft, 200, 100, 0);
            fillModAllowList();
            widgets.add(modAllowList);
        }

        // Disable custom payloads
        if (spoofMode == SpoofMode.CUSTOM) {
            MutableComponent disableCustomPayloadsButtonText = Component.translatable("clientspoofer.option.disable_custom_payloads").append(": ");
            disableCustomPayloadsButtonText.append(ClientSpooferOptions.DISABLE_CUSTOM_PAYLOADS ? OPTION_ON : OPTION_OFF);
            widgets.add(Button.builder(disableCustomPayloadsButtonText, _ -> {
                ClientSpooferOptions.DISABLE_CUSTOM_PAYLOADS = !ClientSpooferOptions.DISABLE_CUSTOM_PAYLOADS;
                rebuildWidgets();
            }).pos(0, 10).size(200, 20).build());
        }

        // Allowed custom payload channels
        if (spoofMode == SpoofMode.CUSTOM) {
            widgets.add(new MultiLineTextWidget(
                    0, 10,
                    Component.translatable("clientspoofer.option.allowed_custom_payload_channels"),
                    font).setMaxWidth(200));

            MultiLineEditBox editBox = MultiLineEditBox.builder().build(
                    font,
                    200, 100,
                    Component.translatable("clientspoofer.option.allowed_custom_payload_channels"));
            editBox.setValue(String.join("\n", ClientSpooferOptions.ALLOWED_CUSTOM_PAYLOAD_CHANNELS));
            editBox.setValueListener(value -> {
                String[] channels = value.split("\n");
                ClientSpooferOptions.ALLOWED_CUSTOM_PAYLOAD_CHANNELS.clear();
                for (String channel : channels) {
                    String trimmed = channel.trim();
                    if (!trimmed.isBlank()) {
                        ClientSpooferOptions.ALLOWED_CUSTOM_PAYLOAD_CHANNELS.add(trimmed);
                    }
                }
            });
            widgets.add(editBox);
        }

        // Done
        widgets.add(Button.builder(Component.translatable("gui.done"), _ -> onClose())
                .size(200, 20)
                .build());

        int y = 5;
        for (AbstractWidget widget : widgets) {
            y += widget.getY();
            widget.setPosition((width - widget.getWidth()) / 2, y);
            y += widget.getHeight() + 5;
            addRenderableWidget(widget);
        }
    }

    @Override
    public void onClose() {
        ClientSpooferOptions.save(ClientSpoofer.CONFIG_FILE);
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
            checkbox = Checkbox.builder(Component.literal(mod.getMetadata().getName()), font)
                    .selected(ClientSpooferOptions.ALLOWED_MODS.contains(mod.getMetadata().getId()))
                    .onValueChange((_, value) -> {
                        String modId = mod.getMetadata().getId();
                        if (value) {
                            ClientSpooferOptions.ALLOWED_MODS.add(modId);
                        } else {
                            ClientSpooferOptions.ALLOWED_MODS.remove(modId);
                        }
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
