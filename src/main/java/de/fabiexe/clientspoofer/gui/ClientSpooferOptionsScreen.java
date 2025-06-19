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
        spoofModeButtonText = spoofModeButtonText.append(switch (ClientSpooferOptions.INSTANCE.spoofMode) {
            case VANILLA -> Component.translatable("clientspoofer.option.spoof_mode.vanilla");
            case NATIVE -> Component.translatable("options.off");
            case CUSTOM -> Component.translatable("clientspoofer.option.spoof_mode.custom");
        });
        widgets.add(Button.builder(spoofModeButtonText, button -> {
            ClientSpooferOptions.INSTANCE.spoofMode = switch (ClientSpooferOptions.INSTANCE.spoofMode) {
                case VANILLA -> SpoofMode.NATIVE;
                case NATIVE -> SpoofMode.CUSTOM;
                case CUSTOM -> SpoofMode.VANILLA;
            };
            rebuildWidgets();
        }).size(200, 20).build());

        // Custom client
        if (ClientSpooferOptions.INSTANCE.spoofMode == SpoofMode.CUSTOM) {
            widgets.add(new MultiLineTextWidget(Component.translatable("clientspoofer.option.custom_client").withStyle(ChatFormatting.GRAY), font));
            EditBox customClientEditBox = new EditBox(font, 0, -5, 200, 20, Component.literal(""));
            customClientEditBox.setValue(ClientSpooferOptions.INSTANCE.customClient);
            customClientEditBox.setResponder(value -> ClientSpooferOptions.INSTANCE.customClient = value);
            widgets.add(customClientEditBox);
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
        ClientSpooferOptions.save(ClientSpoofer.getConfigFile());
        Minecraft.getInstance().setScreen(previous);
    }
}
