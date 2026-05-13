package de.fabiexe.clientspoofer;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class ClientSpoofer implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ClientSpoofer");
    private static Path configFile;
    private static ClientSpooferOptions options;

    @Override
    public void onInitializeClient() {
        configFile = Minecraft.getInstance().gameDirectory.toPath().resolve("config/clientspoofer.json");
        options = ClientSpooferOptions.load(configFile);
    }

    public static @NotNull ClientSpooferOptions getOptions() {
        return options;
    }

    public static void saveOptions() {
        ClientSpooferOptions.save(configFile, options);
    }
}
