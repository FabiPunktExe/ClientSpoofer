package de.fabiexe.clientspoofer.gui;

import de.fabiexe.clientspoofer.ClientSpoofer;
import de.fabiexe.clientspoofer.ClientSpooferOptions;
import de.fabiexe.clientspoofer.SpoofMode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static net.minecraft.network.chat.CommonComponents.*;

public class ClientSpooferOptionsScreen extends Screen {
    private final Screen previous;

    public ClientSpooferOptionsScreen(Screen previous) {
        super(Component.translatable("clientspoofer.options.title"));
        this.previous = previous;
    }

    @Override
    protected void init() {
        List<AbstractWidget> widgets = new ArrayList<>();

        // Spoof mode
        MutableComponent spoofModeButtonText = Component.translatable("clientspoofer.option.spoof_mode").append(": ");
        spoofModeButtonText = spoofModeButtonText.append(switch (ClientSpooferOptions.SPOOF_MODE) {
            case VANILLA -> Component.translatable("clientspoofer.option.spoof_mode.vanilla");
            case OFF -> OPTION_OFF;
            case CUSTOM -> Component.translatable("clientspoofer.option.spoof_mode.custom");
        });
        widgets.add(Button.builder(spoofModeButtonText, button -> {
            ClientSpooferOptions.SPOOF_MODE = switch (ClientSpooferOptions.SPOOF_MODE) {
                case VANILLA -> SpoofMode.OFF;
                case OFF -> SpoofMode.CUSTOM;
                case CUSTOM -> SpoofMode.VANILLA;
            };
            rebuildWidgets();
        }).size(200, 20).build());

        // Custom client
        if (ClientSpooferOptions.SPOOF_MODE == SpoofMode.CUSTOM) {
            widgets.add(new MultiLineTextWidget(Component.translatable("clientspoofer.option.custom_client").withStyle(ChatFormatting.GRAY), font));
            EditBox customClientEditBox = new EditBox(font, 0, -5, 200, 20, Component.literal(""));
            customClientEditBox.setValue(ClientSpooferOptions.CUSTOM_CLIENT);
            customClientEditBox.setResponder(value -> ClientSpooferOptions.CUSTOM_CLIENT = value);
            widgets.add(customClientEditBox);
        }

        // Hide mods
        if (ClientSpooferOptions.SPOOF_MODE == SpoofMode.CUSTOM) {
            MutableComponent hideModsButtonText = Component.translatable("clientspoofer.option.hide_mods").append(": ");
            hideModsButtonText = hideModsButtonText.append(ClientSpooferOptions.HIDE_MODS ? OPTION_ON : OPTION_OFF);
            widgets.add(Button.builder(hideModsButtonText, button -> {
                ClientSpooferOptions.HIDE_MODS = !ClientSpooferOptions.HIDE_MODS;
                rebuildWidgets();
            }).size(200, 20).build());
        }

        // Done
        widgets.add(Button.builder(Component.translatable("gui.done"), button -> onClose()).size(200, 20).build());

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
}
